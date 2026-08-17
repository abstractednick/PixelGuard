package io.github.abstractednick.pixelguard.core

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Safe, lifecycle-neutral bitmap decoding with mandatory target dimensions and an LRU cache.
 *
 * Call [initialize] once from `Application.onCreate()`, then use [decode] from a coroutine.
 */
object PixelGuard {
    private const val TAG = "PixelGuard"

    @Volatile private var state: State? = null

    val isInitialized: Boolean get() = state != null
    val configuration: PixelGuardConfig
        get() = requireState().config

    @Synchronized
    fun initialize(
        context: Context,
        config: PixelGuardConfig = PixelGuardConfig(),
        eventListener: PixelGuardEventListener = PixelGuardEventListener.NONE,
        decodeDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        val activityManager = context.applicationContext
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val maxBytes = (
            activityManager.memoryClass * 1024L * 1024L * config.maxMemoryFraction
            ).toLong().coerceIn(1L, Int.MAX_VALUE.toLong()).toInt()
        state = State(config, PixelGuardCache(maxBytes), eventListener, decodeDispatcher)
        log(config, LogLevel.INFO, "Initialized with ${maxBytes / (1024 * 1024)} MiB cache")
    }

    suspend fun decode(
        source: ImageSource,
        targetWidth: Int,
        targetHeight: Int,
        opaque: Boolean = false,
        cacheKey: String = source.cacheKey,
    ): Bitmap? {
        val current = requireState()
        val key = "$cacheKey:${targetWidth}x$targetHeight:$opaque"
        current.cache.get(key)?.let {
            current.listener.onCacheHit(key)
            return it
        }

        current.listener.onCacheMiss(key)
        return withContext(current.dispatcher) {
            val bitmapConfig = if (opaque) {
                current.config.opaqueBitmapConfig
            } else {
                current.config.defaultBitmapConfig
            }
            val result = BitmapDecoder.decode(
                source = source,
                targetWidth = targetWidth,
                targetHeight = targetHeight,
                config = bitmapConfig,
            ) ?: return@withContext null

            if (
                current.config.enableOversizeWarnings &&
                (result.sourceWidth > targetWidth * current.config.oversizeRatio ||
                    result.sourceHeight > targetHeight * current.config.oversizeRatio)
            ) {
                current.listener.onOversizedSource(
                    key, result.sourceWidth, result.sourceHeight, targetWidth, targetHeight,
                )
                log(
                    current.config,
                    LogLevel.WARN,
                    "Downsampled ${result.sourceWidth}x${result.sourceHeight} to target " +
                        "${targetWidth}x$targetHeight (sample=${result.sampleSize})",
                )
            }
            current.cache.put(key, result.bitmap)
            current.listener.onDecoded(
                key = key,
                width = result.bitmap.width,
                height = result.bitmap.height,
                bytes = result.bitmap.allocationByteCount,
            )
            result.bitmap
        }
    }

    fun cacheSnapshot(): CacheSnapshot = requireState().cache.snapshot()
    fun clearMemoryCache() = requireState().cache.clear()

    @Synchronized
    fun shutdown() {
        state?.cache?.clear()
        state = null
    }

    private fun requireState(): State = checkNotNull(state) {
        "PixelGuard is not initialized. Call PixelGuard.initialize(context) first."
    }

    private fun log(config: PixelGuardConfig, level: LogLevel, message: String) {
        if (config.logLevel == LogLevel.NONE || level.ordinal > config.logLevel.ordinal) return
        when (level) {
            LogLevel.ERROR -> Log.e(TAG, message)
            LogLevel.WARN -> Log.w(TAG, message)
            LogLevel.INFO -> Log.i(TAG, message)
            LogLevel.DEBUG -> Log.d(TAG, message)
            LogLevel.NONE -> Unit
        }
    }

    private data class State(
        val config: PixelGuardConfig,
        val cache: PixelGuardCache,
        val listener: PixelGuardEventListener,
        val dispatcher: CoroutineDispatcher,
    )
}

interface PixelGuardEventListener {
    fun onCacheHit(key: String) = Unit
    fun onCacheMiss(key: String) = Unit
    fun onDecoded(key: String, width: Int, height: Int, bytes: Int) = Unit
    fun onOversizedSource(
        key: String,
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
    ) = Unit

    companion object {
        val NONE: PixelGuardEventListener = object : PixelGuardEventListener {}
    }
}
