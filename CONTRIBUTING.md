# Contributing

Thank you for improving PixelGuard.

1. Open an issue describing the behavior, use case, and expected memory impact.
2. Create a focused branch and keep public API changes backward compatible where possible.
3. Add tests for policy or decoding changes.
4. Run `./gradlew testDebugUnitTest lintDebug assembleDebug`.
5. Submit a pull request with before/after evidence for performance claims.

Code should follow Kotlin conventions, keep Android objects out of long-lived diagnostic state, and
make allocation limits explicit. Security reports must follow [SECURITY.md](SECURITY.md), not a
public issue.
