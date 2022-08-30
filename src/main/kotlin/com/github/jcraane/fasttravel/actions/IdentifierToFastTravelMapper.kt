package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.allIndicesOf
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range

// todo optimize splits and word length (make it even configurable in settings?)
// todo Use modifiers to increase keys to press (or combination of keys like AB). But then you cannot have a single key of A. Modifier
//  key for example is Alt|Option+char. How do we visualize this? With the symbol of the key itself (⌥)?
// todo Exclude comments (we might still need the psi here to exclude them)
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
        // Identifiers are based on the visible text (without the folded regions) to make sure the indexes are based on the correct
        // offsets in the editor. Ignore special characters link < " etc.
        val interestingIdentifiers = visibleText
            .split(' ', '.')
            .asSequence()
            .filter { it.isNotBlank() }
            .filter { ShowFastTravelIdentifiers.ignoredIdentifiers.contains(it).not() }
            .filter { it.length > MIN_WORD_LENGTH }
            .distinct()
            .map { it.trim('\n') }
            .toList()

        val sortedIndicesForIdentifiers = interestingIdentifiers
            .asSequence()
            .map { unfoldedText.allIndicesOf(it) }
            .flatten()
            .filter { index ->
                indexNotPresentInFoldedRegions(foldedRegionRanges, index)
            }
            .distinct().sorted()
            .toList()

        val mapping = sortedIndicesForIdentifiers.mapIndexedNotNull { index, indexForIdentifier ->
            mapIdentifierToFastTravelAction(foldedRegionRanges, indexForIdentifier, index, visibleTextRange)
        }

        return mapping.groupBy { it.identifier }.mapValues { it.value.first().offset }
    }

    /**
     * We want to exclude all indices present in the folded regions since they are not visible in the editor.
     */
    private fun indexNotPresentInFoldedRegions(foldedRegionRanges: List<TextRange>, index: Int) =
        foldedRegionRanges.none() { it.contains(index) }

    private fun indexNotInFoldedRegion(
        indices: List<Int>,
        foldedRegionRanges: List<TextRange>,
    ) = indices.none { index ->
        foldedRegionRanges.none { range ->
            range.contains(index).not()
        }
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

        return if (indexPresentInVisibleRange && index < ShowFastTravelIdentifiers.identifiers.size) {
            val offset = visibleTextRange.startOffset + indexForIdentifier
            FastTravel(ShowFastTravelIdentifiers.identifiers[index], offset)
        } else {
            null
        }
    }

    companion object {
        private const val MIN_WORD_LENGTH = 6
    }
}
