package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.allIndicesOf
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range

// todo optimize splits and word length (make it even configurable in settings?)
// todo Use modifiers to increase keys to press (or combination of keys like AB). But then you cannot have a single key of A
// todo Exclude comments (we might still need the psi here to exclude them)
// todo long file is not mapped correctly in the window (Add testcase for this)
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

        val sortedIndicesForIdentifiers = interestingIdentifiers
            .map { unfoldedText.allIndicesOf(it) }
            .flatten().sorted()

        val mapping = sortedIndicesForIdentifiers.mapIndexedNotNull { index, indexForIdentifier ->
            mapIdentifierToFastTravelAction(foldedRegionRanges, indexForIdentifier, index, visibleTextRange)
        }

        return mapping.groupBy { it.identifier }.mapValues { it.value.first().offset }
    }

    private fun mapIdentifierToFastTravelAction(
        foldedRegionRanges: List<TextRange>,
        indexForIdentifier: Int,
        index: Int,
        visibleTextRange: TextRange,
    ): FastTravel? {
        val indexPresentInVisibleRange = foldedRegionRanges.none { foldedRegion ->
            foldedRegion.range.contains(indexForIdentifier)
        }

        return if (indexPresentInVisibleRange
            && index < ShowFastTravelIdentifiers.identifiers.size
        ) {
            val offset = visibleTextRange.startOffset + indexForIdentifier
            FastTravel(ShowFastTravelIdentifiers.identifiers[index], offset)
        } else {
            null
        }
    }

    companion object {
        private const val MIN_WORD_LENGTH = 5
    }
}
