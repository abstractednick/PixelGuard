# PixelGuard overview
#
# PixelGuard is an Android image & media safety toolkit that packages best-practice
# bitmap handling into a small, opinionated library surface for portfolio and
# production evaluation alike.
#
# Goals
# - Prevent OutOfMemoryError from accidental full-resolution decodes
# - Standardize downsampling, bitmap config selection, and cache budgeting
# - Surface oversized and repeated loads through metadata-only diagnostics
# - Integrate with Coil and Glide instead of replacing them
#
# Modules
# - pixelguard-core          Safe decode API, LRU cache, immutable policy
# - pixelguard-diagnostics   Cache telemetry and developer warnings
# - pixelguard-coil          Bounded Coil 2 request adapter
# - pixelguard-glide         Bounded Glide 4 request adapter
# - sample                   Runnable demo with live cache metrics
#
# See README.md for installation and docs/architecture.md for internals.
