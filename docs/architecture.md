# Architecture

PixelGuard separates allocation policy from image transport. The core module handles local,
repeatable sources; Coil and Glide keep ownership of network and disk pipelines.

## Core decode sequence

1. Build a cache key from source identity, target dimensions, and opacity.
2. Return a live cached bitmap when available.
3. Open the source with `inJustDecodeBounds` to read dimensions without allocating pixels.
4. Select the largest power-of-two sample that still satisfies both target dimensions.
5. Decode on the configured background dispatcher using the selected bitmap config.
6. Emit metadata-only diagnostic events and add the result to the byte-sized LRU cache.

## Memory budget

The default cache budget is 25% of `ActivityManager.memoryClass`. This is configurable from 5% to
50%. Entries are measured with `Bitmap.allocationByteCount`, not object count, because two bitmaps
can differ in cost by orders of magnitude.

The cache never calls `Bitmap.recycle()` during eviction. A view may still hold a bitmap after the
cache releases it, so forced recycling would trade memory pressure for rendering crashes.

## Threading

`PixelGuard.decode` is a suspending API. Cache lookups are immediate and synchronized by
`LruCache`; file/resource reads and pixel allocation execute on `Dispatchers.IO` by default. Tests
may inject a deterministic dispatcher.

## Diagnostics boundary

`PixelGuardEventListener` is defined in core to keep diagnostics optional. The diagnostics module
records atomic counters and immutable warnings. It deliberately does not retain `Bitmap`, `View`,
`Context`, or lifecycle-owner references.

## Failure behavior

- Invalid target dimensions fail fast with `IllegalArgumentException`.
- Unreadable or unsupported sources return `null`.
- Calling the API before initialization fails with an actionable `IllegalStateException`.
- Invalid memory fractions and warning ratios fail during configuration construction.
