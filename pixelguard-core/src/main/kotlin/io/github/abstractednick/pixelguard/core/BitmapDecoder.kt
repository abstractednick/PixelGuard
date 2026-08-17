package io.github.abstractednick.pixelguard.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.max

internal object BitmapDecoder {
    data class Result(
        val bitmap: Bitmap,
        val sourceWidth: Int,
        val sourceHeight: Int,
        val sampleSize: Int,
    )

    fun decode(
        source: ImageSource,
        targetWidth: Int,
        targetHeight: Int,
        config: Bitmap.Config,
    ): Result? {
        require(targetWidth > 0 && targetHeight > 0) { "Target dimensions must be positive" }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        source.open()?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val sampleSize = calculateInSampleSize(
            sourceWidth = bounds.outWidth,
            sourceHeight = bounds.outHeight,
            targetWidth = targetWidth,
            targetHeight = targetHeight,
        )
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = config
            inJustDecodeBounds = false
        }
        val bitmap = source.open()?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null
        return Result(bitmap, bounds.outWidth, bounds.outHeight, sampleSize)
    }

    fun calculateInSampleSize(
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
    ): Int {
        if (sourceWidth <= targetWidth && sourceHeight <= targetHeight) return 1

        // Prefer the larger ratio so neither decoded dimension exceeds the target after sampling.
        val ratio = max(
            sourceWidth.toFloat() / targetWidth,
            sourceHeight.toFloat() / targetHeight,
        )
        var sample = 1
        while (sample * 2 <= ratio) {
            sample *= 2
        }
        return sample
    }
}
