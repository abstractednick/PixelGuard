package io.github.abstractednick.pixelguard.diagnostics

import io.github.abstractednick.pixelguard.core.PixelGuardEventListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Lock-free runtime telemetry listener for debug builds.
 *
 * Pass this instance to `PixelGuard.initialize(..., eventListener = diagnostics)`. It keeps only
 * metadata—not bitmaps—so diagnostics cannot accidentally extend bitmap lifetimes.
 */
class PixelGuardDiagnostics(
    private val duplicateThreshold: Int = 2,
    private val maxRecentWarnings: Int = 50,
) : PixelGuardEventListener {
    private val hits = AtomicInteger()
    private val misses = AtomicInteger()
    private val decodedBytes = AtomicLong()
    private val decodedCount = AtomicInteger()
    private val loadCounts = ConcurrentHashMap<String, AtomicInteger>()
    private val warnings = CopyOnWriteArrayList<DiagnosticWarning>()

    init {
        require(duplicateThreshold >= 2) { "duplicateThreshold must be at least 2" }
        require(maxRecentWarnings > 0) { "maxRecentWarnings must be positive" }
    }

    override fun onCacheHit(key: String) {
        hits.incrementAndGet()
    }

    override fun onCacheMiss(key: String) {
        misses.incrementAndGet()
        val count = loadCounts.getOrPut(key) { AtomicInteger() }.incrementAndGet()
        if (count == duplicateThreshold) {
            addWarning(
                DiagnosticWarning.DuplicateLoad(
                    key = key,
                    loads = count,
                    message = "The same source missed cache $count times; check request sizing.",
                ),
            )
        }
    }

    override fun onDecoded(key: String, width: Int, height: Int, bytes: Int) {
        decodedCount.incrementAndGet()
        decodedBytes.addAndGet(bytes.toLong())
    }

    override fun onOversizedSource(
        key: String,
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
    ) {
        addWarning(
            DiagnosticWarning.OversizedSource(
                key = key,
                sourceSize = "${sourceWidth}x$sourceHeight",
                targetSize = "${targetWidth}x$targetHeight",
                message = "Serve or package an image closer to the target dimensions.",
            ),
        )
    }

    fun snapshot(): DiagnosticsSnapshot = DiagnosticsSnapshot(
        cacheHits = hits.get(),
        cacheMisses = misses.get(),
        decodedImages = decodedCount.get(),
        decodedBytes = decodedBytes.get(),
        warnings = warnings.toList(),
    )

    fun clear() {
        hits.set(0)
        misses.set(0)
        decodedBytes.set(0)
        decodedCount.set(0)
        loadCounts.clear()
        warnings.clear()
    }

    private fun addWarning(warning: DiagnosticWarning) {
        warnings += warning
        while (warnings.size > maxRecentWarnings) warnings.removeAt(0)
    }
}

data class DiagnosticsSnapshot(
    val cacheHits: Int,
    val cacheMisses: Int,
    val decodedImages: Int,
    val decodedBytes: Long,
    val warnings: List<DiagnosticWarning>,
) {
    val cacheHitRate: Float
        get() = cacheHits.toFloat() / (cacheHits + cacheMisses).coerceAtLeast(1)
}

sealed interface DiagnosticWarning {
    val key: String
    val message: String

    data class DuplicateLoad(
        override val key: String,
        val loads: Int,
        override val message: String,
    ) : DiagnosticWarning

    data class OversizedSource(
        override val key: String,
        val sourceSize: String,
        val targetSize: String,
        override val message: String,
    ) : DiagnosticWarning
}
