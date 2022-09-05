package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.actions.identifier.DoubleCharIdentifierGenerator
import com.github.jcraane.fasttravel.actions.identifier.OneCharIdentifierGenerator
import com.github.jcraane.fasttravel.configuration.FastTravelSettingsState
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
    private val fastTravelMapper = IdentifierToFastTravelMapper(
        identifierGenerator = DoubleCharIdentifierGenerator()
    )

    override fun run() {
        val config = FastTravelSettingsState.getInstance()
        val visibleTextRange = editor.getVisibleTextRange()
        val allText = editor.document.getText(visibleTextRange)
        val visibleText = removeFoldedRegions(allText, visibleTextRange)

        val foldedRegionRanges = visibleText.second
            .map { it.range }

        val mapping = fastTravelMapper.getFastTravelMappings(
            allText,
            visibleText.first,
            foldedRegionRanges,
            visibleTextRange,
            config.minWordLength,
        )

        val fastTravelIdentifierPanel = FastTravelIdentifierPanel(editor, mapping, config.getForeGroundColor(), config.getBackgroundColor())
        fastTravelKeyListener.removeFastTravelIdentifierPanel()

        fastTravelKeyListener.fastTravelMapping = mapping
        fastTravelKeyListener.fastTravelIdentifierPanel = fastTravelIdentifierPanel
        editor.contentComponent.addKeyListener(fastTravelKeyListener)
        editor.contentComponent.add(fastTravelIdentifierPanel)
        fastTravelIdentifierPanel.repaint()
        editor.contentComponent.repaint()
    }

    private fun removeFoldedRegions(visibleText: String, visibleTextRange: TextRange): Pair<String, List<FoldRegion>> {
        var mutableText = visibleText

        val foldedRegions = editor.foldingModel.allFoldRegions
            .filter { visibleTextRange.contains(it) }
            .filter { it.isExpanded.not() }

        foldedRegions
            .forEach { foldedRegion ->
                val startOffset = foldedRegion.range.startOffset
                val line = editor.yToVisualLine(startOffset)

                if (line < editor.document.lineCount) {
                    val endOffset = foldedRegion.range.endOffset
                    val lineStartOffset = editor.document.getLineStartOffset(line)

                    val isFoldedRegionInBoundsOfVisibleText = (lineStartOffset + endOffset) < visibleText.length
                    if (isFoldedRegionInBoundsOfVisibleText && endOffset > lineStartOffset) {
                        mutableText = mutableText.replace(visibleText.substring(lineStartOffset, endOffset), "")
                    }
                }
            }

        return mutableText to foldedRegions
    }

    companion object {
        val ignoredIdentifiers = listOf("import")
    }
}

data class FastTravel(val identifier: String, val offset: Int)
