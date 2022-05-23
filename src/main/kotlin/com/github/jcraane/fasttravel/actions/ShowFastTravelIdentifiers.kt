package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.getVisibleTextRange
import com.github.jcraane.fasttravel.renderer.FastTravelIdentifierPanel
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range

class ShowFastTravelIdentifiers(
    private val editor: Editor,
    private val fastTravelKeyListener: FastTravelKeyListener,
) : Runnable {
    override fun run() {
        val visibleTextRange = editor.getVisibleTextRange()
        val allText = editor.document.getText(visibleTextRange)
        val visibleText = removeFoldedRegions(allText)

//        todo offsets are not correct yet because we removed the folded regions.
        // Text can be removed before AND after the identifier, need to take this into account
        val mapping = getFastTravelMappings(allText, visibleText, visibleTextRange)

        val fastTravelIdentifierPanel = FastTravelIdentifierPanel(editor, mapping)
        fastTravelKeyListener.removeFastTravelIdentifierPanel()

        fastTravelKeyListener.fastTravelMapping = mapping
        fastTravelKeyListener.fastTravelIdentifierPanel = fastTravelIdentifierPanel
        editor.contentComponent.addKeyListener(fastTravelKeyListener)
        editor.contentComponent.add(fastTravelIdentifierPanel)
        fastTravelIdentifierPanel.repaint()
        editor.contentComponent.repaint()
    }

    private fun removeFoldedRegions(visibleText: @NlsSafe String): @NlsSafe String {
        var visibleText1 = visibleText
        editor.foldingModel.allFoldRegions.forEach { foldRegion ->
            val startOffset = foldRegion.range.startOffset
            val endOffset = foldRegion.range.endOffset

            val line = editor.yToVisualLine(startOffset)
            if (line < editor.document.lineCount) {
                val lineStartOffset = editor.document.getLineStartOffset(line)

                if (foldRegion.isExpanded.not()) {
                    visibleText1 = visibleText1.removeRange(lineStartOffset..endOffset)
                }
            }
        }
        return visibleText1
    }

    //        todo optimize splits and word length (make it even configurable?)
    private fun getFastTravelMappings(
        unfoldedText: String,
        visibleText: String,
        visibleTextRange: TextRange,
    ): Map<String, Int> {
        // Ignore special characters link < " etc.
        val interestingIdentifiers = visibleText
            .split(' ', '.')
            .filter { it.isNotBlank() }
            .filter { it.length > MIN_WORD_LENGTH }
            .map { it.trim('\n') }
            .toSet()

        var identifierIndex = 0
        val mapping = interestingIdentifiers.map { identifier ->
            var index = unfoldedText.indexOf(identifier)
            val fastTravelers = mutableListOf<FastTravel>()

            while (index > 0) {
                val offset = visibleTextRange.startOffset + index
                if (identifierIndex < identifiers.size) {
                    fastTravelers += FastTravel(identifiers[identifierIndex], offset)
                }
                identifierIndex++
                index = unfoldedText.indexOf(identifier, startIndex = index + 1)
            }

            fastTravelers
        }.flatten()

        return mapping.groupBy { it.identifier }.mapValues { it.value.first().offset }
    }

    companion object {
        private val LOG = logger<FastTravelAction>()

        private val numbers = (0..9).toList().map { it.toString() }
        private val upperCase = ('A'..'Z').toList().map { it.toString() }
        private val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = lowerCase + upperCase + numbers
        private const val MIN_WORD_LENGTH = 5
    }
}

data class FastTravel(val identifier: String, val offset: Int)
