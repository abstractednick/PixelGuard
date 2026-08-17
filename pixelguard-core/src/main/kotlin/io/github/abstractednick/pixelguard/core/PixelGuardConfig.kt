package io.github.abstractednick.pixelguard.core

import android.graphics.Bitmap

/**
 * Immutable safety policy used by [PixelGuard].
 *
 * A configuration is supplied during initialization so behavior remains predictable across
 * threads. The defaults favor image quality while limiting the memory cache to 25% of the heap.
 */
data class PixelGuardConfig(
    val maxMemoryFraction: Float = 0.25f,
    val defaultBitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888,
    val opaqueBitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565,
    val enableDuplicateDetection: Boolean = true,
    val enableOversizeWarnings: Boolean = true,
    val oversizeRatio: Float = 2f,
    val logLevel: LogLevel = LogLevel.INFO,
) {
    init {
        require(maxMemoryFraction in 0.05f..0.5f) {
            "maxMemoryFraction must be between 0.05 and 0.5"
        }
        require(oversizeRatio >= 1f) { "oversizeRatio must be at least 1" }
    }
}

enum class LogLevel { NONE, ERROR, WARN, INFO, DEBUG }
