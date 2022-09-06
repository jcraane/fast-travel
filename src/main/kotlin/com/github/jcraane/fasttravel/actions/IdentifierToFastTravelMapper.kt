package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.actions.identifier.IdentifierGenerator
import com.github.jcraane.fasttravel.extensions.allIndicesOf
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
import java.util.regex.Pattern

class IdentifierToFastTravelMapper(
    private val identifierGenerator: IdentifierGenerator,
) {
    /**
     * Maps identifiers from the visible text to [FastTravel] actions.
     */
    fun getFastTravelMappings(
        unfoldedText: String,
        visibleText: String,
        foldedRegionRanges: List<TextRange>,
        visibleTextRange: TextRange,
        minWordLength: Int,
        useCamelHumps: Boolean,
    ): Map<String, Int> {
        // Identifiers are based on the visible text (without the folded regions) to make sure the indexes are based on the correct
        // offsets in the editor. Ignore special characters link < " etc.
        val interestingIdentifiers = visibleText
            .split(' ', '.', '(', ')')
            .asSequence()
            .map { it ->
                splitCamelCase(it, useCamelHumps)
            }
            .flatten()
            .distinct()
            .filter { it.isNotBlank() }
            .filter { ShowFastTravelIdentifiers.ignoredIdentifiers.contains(it).not() }
            .filter { it.length >= minWordLength }
            .map { it.trim('\n', '(', ')', '$', '#', '{', '}', '[', ']', ';') }
            .toList()

        val sortedIndicesForIdentifiers = interestingIdentifiers
            .asSequence()
            .map { unfoldedText.allIndicesOf(it) }
            .flatten()
            .filter { index -> indexNotPresentInFoldedRegions(foldedRegionRanges, index) }
            .distinct().sorted()
            .toList()

        val mapping = sortedIndicesForIdentifiers.mapIndexedNotNull { index, indexForIdentifier ->
            mapIdentifierToFastTravelAction(foldedRegionRanges, indexForIdentifier, index, visibleTextRange)
        }

        return mapping.groupBy { it.identifier }.mapValues { it.value.first().offset }
    }

    private fun splitCamelCase(text: String, useCamelHumps: Boolean): List<String> {
        return if (useCamelHumps) {
            text.split(camelCaseSplitRegex)
        } else {
            listOf(text)
        }
    }

    /**
     * We want to exclude all indices present in the folded regions since they are not visible in the editor.
     */
    private fun indexNotPresentInFoldedRegions(foldedRegionRanges: List<TextRange>, index: Int) =
        foldedRegionRanges.none() { it.contains(index) }

    private fun mapIdentifierToFastTravelAction(
        foldedRegionRanges: List<TextRange>,
        indexForIdentifier: Int,
        index: Int,
        visibleTextRange: TextRange,
    ): FastTravel? {
        val indexPresentInVisibleRange = foldedRegionRanges.none { foldedRegion ->
            foldedRegion.range.contains(indexForIdentifier)
        }

        return if (indexPresentInVisibleRange && identifierGenerator.hasNext()) {
            val offset = visibleTextRange.startOffset + indexForIdentifier
            FastTravel(identifierGenerator.next(), offset)
        } else {
            null
        }
    }

    companion object {
        private val camelCaseSplitRegex = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
    }
}
