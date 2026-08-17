package io.github.abstractednick.pixelguard.coil

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import coil.ImageLoader
import coil.decode.BitmapFactoryDecoder
import coil.request.ImageRequest
import coil.size.Precision

/** Coil 2 adapter that makes target size and bitmap policy explicit. */
object PixelGuardCoil {
    fun createImageLoader(
        context: Context,
        allowRgb565: Boolean = true,
        crossfade: Boolean = true,
    ): ImageLoader = ImageLoader.Builder(context)
        .components {
            add(BitmapFactoryDecoder.Factory())
        }
        .allowRgb565(allowRgb565)
        .crossfade(crossfade)
        .respectCacheHeaders(true)
        .build()

    fun load(
        imageLoader: ImageLoader,
        data: Any,
        targetView: ImageView,
        options: PixelGuardCoilOptions,
    ) {
        val request = ImageRequest.Builder(targetView.context)
            .data(data)
            .target(targetView)
            .size(options.maxWidth, options.maxHeight)
            .precision(Precision.INEXACT)
            .bitmapConfig(if (options.opaque) Bitmap.Config.RGB_565 else Bitmap.Config.ARGB_8888)
            .crossfade(options.crossfade)
            .build()
        imageLoader.enqueue(request)
    }
}

data class PixelGuardCoilOptions(
    val maxWidth: Int,
    val maxHeight: Int,
    val opaque: Boolean = false,
    val crossfade: Boolean = true,
) {
    init {
        require(maxWidth > 0 && maxHeight > 0) { "Maximum dimensions must be positive" }
    }
}
