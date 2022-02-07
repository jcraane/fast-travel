package com.github.jcraane.fasttravel.extensions

import junit.framework.TestCase
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertTrue

class IntRangeExtensionsKtTest {
    @Test
    fun withinRange() {
        assertTrue((5..8).within(0..10))
        assertFalse((5..8).within(6..10))
        assertFalse((3..8).within(4..11))
        assertFalse((5..8).within(10..14))
    }
}
