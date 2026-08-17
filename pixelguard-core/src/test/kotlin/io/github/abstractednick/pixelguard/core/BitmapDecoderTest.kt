package io.github.abstractednick.pixelguard.core

import org.junit.Assert.assertEquals
import org.junit.Test

class BitmapDecoderTest {
    @Test
    fun `returns one when source already fits target`() {
        assertEquals(1, BitmapDecoder.calculateInSampleSize(400, 300, 800, 600))
    }

    @Test
    fun `chooses largest safe power of two sample`() {
        assertEquals(4, BitmapDecoder.calculateInSampleSize(4000, 3000, 800, 600))
    }

    @Test
    fun `uses the larger dimension ratio`() {
        // Width needs sample 4 (4000/800=5), height alone would only need sample 1.
        assertEquals(4, BitmapDecoder.calculateInSampleSize(4000, 1000, 800, 600))
    }
}
