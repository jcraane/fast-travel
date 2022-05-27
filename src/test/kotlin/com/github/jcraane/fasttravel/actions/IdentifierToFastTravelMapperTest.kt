package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.util.TextRange
import org.junit.Assert
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

        Assert.assertEquals(mappings.values.sorted(), mappings.values.map { it })
    }

    @Test
    fun mapIdentifiersInTextWithFoldedRegionsBeforeVisibleArea() {
        val unfoldedText =
            IdentifierToFastTravelMapperTest::class.java.getResource("/foldedRegionsBeforeVisibleArea/unfoldedText.txt").readText()
        val visibleText = IdentifierToFastTravelMapperTest::class.java.getResource("/foldedRegionsBeforeVisibleArea/visibleText.txt")
            .readText()

        val mappings = mapper.getFastTravelMappings(
            unfoldedText = unfoldedText,
            visibleText = visibleText,
            foldedRegionRanges = listOf(TextRange(54, 435)),
            visibleTextRange = TextRange(0, 1986)
        )

        println(mappings)
    }

    fun mapIdentifiersInTextWithFoldedRegionsBeforeAndAfterVisibleArea() {

    }
}
