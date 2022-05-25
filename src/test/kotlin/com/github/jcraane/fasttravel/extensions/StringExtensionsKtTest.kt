package com.github.jcraane.fasttravel.extensions

import junit.framework.TestCase
import org.junit.Test

class StringExtensionsKtTest {
    @Test
    fun getAllIndicesInString() {
        val text = """
        fun sayHello2(name: String) {
            val greeting = "Hello World"
            println(greeting)
        }

        fun sayHello(name: String) {
            val greeting = "Hello World"
            println(greeting)
        }
    """.trimIndent()

        TestCase.assertEquals(listOf(38, 75, 125, 162), text.allIndicesOf("greeting"))
        TestCase.assertTrue(text.allIndicesOf("none").isEmpty())
    }
}
