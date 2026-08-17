# Adoption and migration

PixelGuard is designed for screen-by-screen adoption. Existing Coil and Glide pipelines do not need
to be replaced.

## 1. Establish a memory policy

Initialize in `Application.onCreate()`. Start with the 25% cache default, measure representative
low-memory devices, and lower the fraction if images compete with large databases, maps, or video.
Enable diagnostics in debug builds only.

## 2. Replace manual decodes

Before:

```kotlin
val bitmap = BitmapFactory.decodeFile(path)
imageView.setImageBitmap(bitmap)
```

After:

```kotlin
lifecycleScope.launch {
    imageView.setImageBitmap(
        PixelGuard.decode(
            ImageSource.FileSource(path),
            targetWidth = imageView.width,
            targetHeight = imageView.height,
        ),
    )
}
```

Wait until a view is laid out before reading its dimensions, or pass dimensions from the UI spec.
Use `opaque = true` only when transparency is not required.

## 3. Bound image-loader requests

Replace direct `load(...).into(...)` calls at high-risk surfaces—feeds, galleries, carousels—with
the matching PixelGuard adapter. Choose maximum dimensions from the largest rendered size, not from
the source asset.

## 4. Review diagnostics

Inspect `PixelGuardDiagnostics.snapshot()` after exercising long scrolls, configuration changes,
and navigation loops. Repeated cache misses often indicate unstable URLs or inconsistent target
sizes. Oversize warnings usually justify a resized CDN variant or density-specific packaged asset.

## 5. Roll out safely

- Adopt one image-heavy screen first.
- Compare peak heap, allocation count, frame time, and network bytes before and after.
- Test API 21 and a current Android version.
- Exercise process recreation and low-memory background/foreground transitions.
- Expand only after metrics remain stable.
