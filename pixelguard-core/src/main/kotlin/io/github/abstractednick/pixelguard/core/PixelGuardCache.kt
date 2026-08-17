package io.github.abstractednick.pixelguard.core

import android.graphics.Bitmap
import android.util.LruCache

/** Thread-safe byte-sized LRU cache with observable usage statistics. */
class PixelGuardCache(maxBytes: Int) {
    init {
        require(maxBytes > 0) { "maxBytes must be positive" }
    }

    private val cache = object : LruCache<String, Bitmap>(maxBytes) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
    }

    @Synchronized
    fun get(key: String): Bitmap? = cache.get(key)?.takeUnless(Bitmap::isRecycled)

    @Synchronized
    fun put(key: String, bitmap: Bitmap): Bitmap {
        if (!bitmap.isRecycled) cache.put(key, bitmap)
        return bitmap
    }

    @Synchronized
    fun remove(key: String): Bitmap? = cache.remove(key)

    @Synchronized
    fun clear() = cache.evictAll()

    @Synchronized
    fun snapshot(): CacheSnapshot = CacheSnapshot(
        sizeBytes = cache.size(),
        maxSizeBytes = cache.maxSize(),
        entries = cache.snapshot().size,
        hits = cache.hitCount(),
        misses = cache.missCount(),
        evictions = cache.evictionCount(),
    )
}

data class CacheSnapshot(
    val sizeBytes: Int,
    val maxSizeBytes: Int,
    val entries: Int,
    val hits: Int,
    val misses: Int,
    val evictions: Int,
) {
    val utilization: Float get() = sizeBytes.toFloat() / maxSizeBytes
    val hitRate: Float get() = hits.toFloat() / (hits + misses).coerceAtLeast(1)
}
