package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.allIndicesOf
import com.github.jcraane.fasttravel.extensions.getVisibleTextRange
import com.github.jcraane.fasttravel.renderer.FastTravelIdentifierPanel
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
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

        val mapping = getFastTravelMappings(allText, visibleText.first, visibleText.second, visibleTextRange)

        val fastTravelIdentifierPanel = FastTravelIdentifierPanel(editor, mapping)
        fastTravelKeyListener.removeFastTravelIdentifierPanel()

        fastTravelKeyListener.fastTravelMapping = mapping
        fastTravelKeyListener.fastTravelIdentifierPanel = fastTravelIdentifierPanel
        editor.contentComponent.addKeyListener(fastTravelKeyListener)
        editor.contentComponent.add(fastTravelIdentifierPanel)
        fastTravelIdentifierPanel.repaint()
        editor.contentComponent.repaint()
    }

    private fun removeFoldedRegions(visibleText: String): Pair<String, List<FoldRegion>> {
        var mutableText = visibleText

        val foldedRegions = editor.foldingModel.allFoldRegions
            .filter { it.isExpanded.not() }

        foldedRegions
            .forEach { foldedRegion ->
                val startOffset = foldedRegion.range.startOffset
                val line = editor.yToVisualLine(startOffset)

                if (line < editor.document.lineCount) {
                    val endOffset = foldedRegion.range.endOffset
                    val lineStartOffset = editor.document.getLineStartOffset(line)
                    mutableText = mutableText.replace(visibleText.substring(lineStartOffset, endOffset), "")
                }
            }

        return mutableText to foldedRegions
    }

    //        todo optimize splits and word length (make it even configurable?)
    private fun getFastTravelMappings(
        unfoldedText: String,
        visibleText: String,
        foldedRegions: List<FoldRegion>,
        visibleTextRange: TextRange,
    ): Map<String, Int> {
        // Identifiers are based on the visible text (without the folded regions)
        // Ignore special characters link < " etc.
        val interestingIdentifiers = visibleText
            .split(' ', '.')
            .filter { it.isNotBlank() }
            .filter { ignoredIdentifiers.contains(it).not() }
            .filter { it.length > MIN_WORD_LENGTH }
            .map { it.trim('\n') }
            .toSet()

//        todo sort all indices ascending so identifiers also appear ascending in the text
        var identifierIndex = 0
        val mapping = interestingIdentifiers.map { identifier ->
            val indices = unfoldedText.allIndicesOf(identifier)
            val fastTravelers = mutableListOf<FastTravel>()

            val indicesPresentInVisibleText = indices.filter { index ->
                foldedRegions.none { foldedRegion ->
                    foldedRegion.range.contains(index)
                }
            }

            indicesPresentInVisibleText.map { index ->
                val offset = visibleTextRange.startOffset + index
                if (identifierIndex < identifiers.size) {
                    fastTravelers += FastTravel(identifiers[identifierIndex], offset)
                }
                identifierIndex++
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
        val ignoredIdentifiers = listOf("import")
        private const val MIN_WORD_LENGTH = 5
    }
}

data class FastTravel(val identifier: String, val offset: Int)
