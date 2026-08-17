package io.github.abstractednick.pixelguard.sample

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import io.github.abstractednick.pixelguard.coil.PixelGuardCoil
import io.github.abstractednick.pixelguard.coil.PixelGuardCoilOptions
import io.github.abstractednick.pixelguard.core.PixelGuard

class MainActivity : AppCompatActivity() {
    private lateinit var imageLoader: ImageLoader
    private lateinit var metrics: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader = PixelGuardCoil.createImageLoader(this)

        val image = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(280),
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(Color.rgb(46, 44, 50))
        }
        metrics = TextView(this).apply {
            setTextColor(Color.LTGRAY)
            textSize = 15f
            setPadding(0, dp(20), 0, dp(20))
        }
        val button = Button(this).apply {
            text = "Load safely"
            setOnClickListener {
                PixelGuardCoil.load(
                    imageLoader = imageLoader,
                    data = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1600",
                    targetView = image,
                    options = PixelGuardCoilOptions(800, 560),
                )
                renderMetrics()
            }
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(48), dp(24), dp(24))
            setBackgroundColor(Color.rgb(28, 27, 31))
            addView(TextView(context).apply {
                text = "PixelGuard"
                textSize = 32f
                setTextColor(Color.WHITE)
                setPadding(0, 0, 0, dp(20))
            })
            addView(image)
            addView(metrics)
            addView(button)
        }
        setContentView(content)
        renderMetrics()
    }

    private fun renderMetrics() {
        val cache = PixelGuard.cacheSnapshot()
        val diagnostics = (application as PixelGuardSampleApp).diagnostics.snapshot()
        metrics.text = buildString {
            appendLine("Cache: ${cache.sizeBytes / 1024} KiB / ${cache.maxSizeBytes / 1024} KiB")
            appendLine("Hits: ${diagnostics.cacheHits}  Misses: ${diagnostics.cacheMisses}")
            append("Warnings: ${diagnostics.warnings.size}")
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
