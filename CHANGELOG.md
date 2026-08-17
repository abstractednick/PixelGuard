# Changelog

All notable changes are documented here. The project follows
[Semantic Versioning](https://semver.org/).

## 1.0.0 - 2026-08-17

### Added

- Repeatable file, content URI, resource, and byte-array image sources.
- Two-pass, background bitmap decoding with power-of-two downsampling.
- Heap-aware byte-sized LRU memory cache and cache statistics.
- Immutable bitmap config and oversize-warning policy.
- Metadata-only diagnostics for cache activity, duplicate misses, and oversized sources.
- Bounded request adapters for Coil 2 and Glide 4.
- Runnable sample application, automated tests, Android lint, and CI.
