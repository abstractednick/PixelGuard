package io.github.abstractednick.pixelguard.sample

import android.app.Application
import io.github.abstractednick.pixelguard.core.LogLevel
import io.github.abstractednick.pixelguard.core.PixelGuard
import io.github.abstractednick.pixelguard.core.PixelGuardConfig
import io.github.abstractednick.pixelguard.diagnostics.PixelGuardDiagnostics

class PixelGuardSampleApp : Application() {
    val diagnostics = PixelGuardDiagnostics()

    override fun onCreate() {
        super.onCreate()
        PixelGuard.initialize(
            context = this,
            config = PixelGuardConfig(logLevel = LogLevel.DEBUG),
            eventListener = diagnostics,
        )
    }
}
