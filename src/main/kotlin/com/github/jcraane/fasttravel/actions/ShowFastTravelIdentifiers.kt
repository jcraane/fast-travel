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
    private val fastTravelMapper = IdentifierToFastTravelMapper()

    override fun run() {
        val visibleTextRange = editor.getVisibleTextRange()
        val allText = editor.document.getText(visibleTextRange)
        val visibleText = removeFoldedRegions(allText)

        val foldedRegionRanges = visibleText.second
            .map { it.range }
            .filter { visibleTextRange.contains(it) }

        val mapping = fastTravelMapper.getFastTravelMappings(
            allText,
            visibleText.first,
            foldedRegionRanges,
            visibleTextRange
        )

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

                    val isFoldedRegionInBoundsOfVisibleText = (lineStartOffset + endOffset) < visibleText.length
                    if (isFoldedRegionInBoundsOfVisibleText) {
                        mutableText = mutableText.replace(visibleText.substring(lineStartOffset, endOffset), "")
                    }
                }
            }

        return mutableText to foldedRegions
    }

    companion object {
        private val LOG = logger<FastTravelAction>()

        private val numbers = (0..9).toList().map { it.toString() }
        private val upperCase = ('A'..'Z').toList().map { it.toString() }
        private val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = lowerCase + upperCase + numbers
        val ignoredIdentifiers = listOf("import")
    }
}

data class FastTravel(val identifier: String, val offset: Int)
