package io.github.abstractednick.pixelguard.glide

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/** Glide adapter that refuses unbounded requests. */
object PixelGuardGlide {
    fun load(
        model: Any,
        targetView: ImageView,
        options: PixelGuardGlideOptions,
    ) {
        val requestOptions = RequestOptions()
            .override(options.maxWidth, options.maxHeight)
            .format(
                if (options.opaque) {
                    DecodeFormat.PREFER_RGB_565
                } else {
                    DecodeFormat.PREFER_ARGB_8888
                },
            )
            .diskCacheStrategy(options.diskCacheStrategy)

        Glide.with(targetView)
            .load(model)
            .apply(requestOptions)
            .into(targetView)
    }

    fun clear(targetView: ImageView) {
        Glide.with(targetView).clear(targetView)
    }
}

data class PixelGuardGlideOptions(
    val maxWidth: Int,
    val maxHeight: Int,
    val opaque: Boolean = false,
    val diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
) {
    init {
        require(maxWidth > 0 && maxHeight > 0) { "Maximum dimensions must be positive" }
    }
}
