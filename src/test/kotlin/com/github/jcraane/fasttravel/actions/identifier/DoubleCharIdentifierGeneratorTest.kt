package com.github.jcraane.fasttravel.actions.identifier

import com.intellij.testFramework.assertEqualsToFile
import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleCharIdentifierGeneratorTest {
    @Test
    fun generateIdentifiers() {
        val generator = DoubleCharIdentifierGenerator()
        assertEquals("aa", generator.next())
        assertEquals("ab", generator.next())
        assertEquals("ac", generator.next())
        assertEquals("ad", generator.next())
        assertEquals("ae", generator.next())
    }

    @Test
    fun verifyCorrectTransitionToNewMajor() {
        val generator = DoubleCharIdentifierGenerator(initialMajor = 'a', initialMinor = 'z')
        assertEquals("az", generator.next())
        assertEquals("ba", generator.next())
    }
}
