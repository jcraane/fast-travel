package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.util.TextRange
import org.junit.Test

class IdentifierToFastTravelMapperTest {
    private val mapper = IdentifierToFastTravelMapper()

    @Test
    fun mapIdentifiersInTextWithoutFoldedRegions() {
        val text = """
        fun sayHello2(name: String) {
            val greeting = "Hello World"
            println(greeting)
        }
    """.trimIndent()

        val mappings = mapper.getFastTravelMappings(
            unfoldedText = text,
            visibleText = text,
            foldedRegionRanges = emptyList(),
            visibleTextRange = TextRange(0, text.length)
        )
        println(mappings)
    }

    fun mapIdentifiersInTextWithFoldedRegionsBeforeVisibleArea() {

    }

    fun mapIdentifiersInTextWithFoldedRegionsBeforeAndAfterVisibleArea() {

    }
}
