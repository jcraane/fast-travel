package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.allIndicesOf
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range

//        todo sort all indices ascending so identifiers also appear ascending in the text
//        todo optimize splits and word length (make it even configurable?)
class IdentifierToFastTravelMapper {
    /**
     * Maps identifiers from the visible text to [FastTravel] actions.
     */
    fun getFastTravelMappings(
        unfoldedText: String,
        visibleText: String,
        foldedRegionRanges: List<TextRange>,
        visibleTextRange: TextRange,
    ): Map<String, Int> {
        // Identifiers are based on the visible text (without the folded regions)
        // Ignore special characters link < " etc.
        val interestingIdentifiers = visibleText
            .split(' ', '.')
            .filter { it.isNotBlank() }
            .filter { ShowFastTravelIdentifiers.ignoredIdentifiers.contains(it).not() }
            .filter { it.length > MIN_WORD_LENGTH }
            .map { it.trim('\n') }
            .toSet()

        var identifierIndex = 0
        val mapping = interestingIdentifiers.map { identifier ->
            val indices = unfoldedText.allIndicesOf(identifier)
            val fastTravelers = mutableListOf<FastTravel>()

            val indicesPresentInVisibleText = indices.filter { index ->
                foldedRegionRanges.none { foldedRegion ->
                    foldedRegion.range.contains(index)
                }
            }

            indicesPresentInVisibleText.map { index ->
                val offset = visibleTextRange.startOffset + index
                if (identifierIndex < ShowFastTravelIdentifiers.identifiers.size) {
                    fastTravelers += FastTravel(ShowFastTravelIdentifiers.identifiers[identifierIndex], offset)
                }
                identifierIndex++
            }

            fastTravelers
        }.flatten()

        return mapping.groupBy { it.identifier }.mapValues { it.value.first().offset }
    }

    companion object {
        private const val MIN_WORD_LENGTH = 5
    }
}
