# Testing and performance

## Automated checks

Run the same gate used by CI:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Core unit tests validate downsampling boundaries. Library and sample compilation catches API drift
across PixelGuard, Coil, and Glide.

## Profiler scenario

Use an API 21 emulator or low-memory physical device and Android Studio's Memory Profiler:

1. Record idle heap after a clean launch.
2. Open a grid containing at least 100 mixed-dimension images.
3. Scroll from start to end and back three times.
4. Navigate away, force GC, and record retained heap.
5. Repeat with diagnostics enabled.

Track peak heap, post-GC retained heap, allocation churn, cache hit rate, and dropped frames. The
diagnostics-on result should remain close to diagnostics-off because telemetry retains no bitmaps.

## Suggested acceptance budgets

- No `OutOfMemoryError` through the full scenario.
- Post-GC retained heap returns within 15% of the second idle baseline.
- A warmed second scroll has a higher cache hit rate than the first.
- No decoded bitmap exceeds the documented UI maximum without an oversize warning.
- Diagnostics add no retained `Activity`, `View`, or `Bitmap` instances.

Budgets are starting points, not universal guarantees. Set release thresholds from production device
distribution and each app's non-image memory footprint.
