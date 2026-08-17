package io.github.abstractednick.pixelguard.core

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import java.io.File
import java.io.InputStream

/** A repeatable source that PixelGuard can open once for bounds and again for decoding. */
sealed interface ImageSource {
    val cacheKey: String
    fun open(): InputStream?

    data class FileSource(val file: File) : ImageSource {
        constructor(path: String) : this(File(path))
        override val cacheKey: String = "file:${file.absolutePath}:${file.lastModified()}"
        override fun open(): InputStream? = file.takeIf(File::isFile)?.inputStream()?.buffered()
    }

    data class ContentUri(
        val resolver: ContentResolver,
        val uri: Uri,
    ) : ImageSource {
        override val cacheKey: String = "content:$uri"
        override fun open(): InputStream? = resolver.openInputStream(uri)?.buffered()
    }

    data class Resource(
        val resources: Resources,
        val resourceId: Int,
    ) : ImageSource {
        override val cacheKey: String = "resource:$resourceId"
        override fun open(): InputStream = resources.openRawResource(resourceId).buffered()
    }

    /**
     * Creates a source backed by a byte array. Prefer this over a one-shot [InputStream], because
     * safe decoding requires two reads.
     */
    data class Bytes(
        val data: ByteArray,
        override val cacheKey: String = "bytes:${data.contentHashCode()}:${data.size}",
    ) : ImageSource {
        override fun open(): InputStream = data.inputStream()
    }
}
