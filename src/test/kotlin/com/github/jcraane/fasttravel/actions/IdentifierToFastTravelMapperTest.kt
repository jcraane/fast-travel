package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.actions.identifier.OneCharIdentifierGenerator
import com.intellij.openapi.util.TextRange
import org.intellij.lang.annotations.Language
import org.junit.Assert
import org.junit.Test

class IdentifierToFastTravelMapperTest {
    private val mapper = IdentifierToFastTravelMapper(
        identifierGenerator = OneCharIdentifierGenerator(),
    )

    @Test
    fun mapIdentifiersInTextWithoutFoldedRegions() {
        @Language("kotlin") val text = """
            fun sayHello2(name: String) {
                val greeting = "Hello World"
                println(greeting)
            }
        """.trimIndent()

        val mappings = mapper.getFastTravelMappings(
            unfoldedText = text,
            visibleText = text,
            foldedRegionRanges = emptyList(),
            visibleTextRange = TextRange(0, text.length),
            minWordLength = 5,
            useCamelHumps = true,
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
            visibleTextRange = TextRange(0, 1986),
            minWordLength = 5,
            useCamelHumps = true,
        )

        println(mappings)
    }

    @Test
    fun splitWithCamelHumps() {
        @Language("kotlin") val text = """
            class ViewController {
                fun doSomethingReallyImportant() {
                    print("Important stuff going on")
                }        
            }
        """.trimIndent()

        val mappings = mapper.getFastTravelMappings(
            unfoldedText = text,
            visibleText = text,
            foldedRegionRanges = emptyList(),
            visibleTextRange = TextRange(0, text.length),
            minWordLength = 5,
            useCamelHumps = true,
        )

        Assert.assertEquals(mapOf(
            "a" to 0,
            "b" to 10,
            "c" to 33,
            "d" to 42,
            "e" to 48,
            "f" to 70,
            "g" to 77,
            "h" to 87,
            "i" to 93,
        ), mappings)
    }
}
