package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.getVisibleTextRange
import com.github.jcraane.fasttravel.renderer.FastTravelIdentifierPanel
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class ShowFastTravelIdentifiers(
    private val editor: Editor,
    private val fastTravelKeyListener: FastTravelKeyListener,
) : Runnable {
    override fun run() {
        val visibleTextRange = editor.getVisibleTextRange()
        val visibleText = editor.document.getText(visibleTextRange)
        val mapping = getFastTravelMappings(visibleText, visibleTextRange)

        val fastTravelIdentifierPanel = FastTravelIdentifierPanel(editor, mapping)
        fastTravelKeyListener.removeFastTravelIdentifierPanel()

        fastTravelKeyListener.fastTravelMapping = mapping
        fastTravelKeyListener.fastTravelIdentifierPanel = fastTravelIdentifierPanel
        editor.contentComponent.addKeyListener(fastTravelKeyListener)
        editor.contentComponent.add(fastTravelIdentifierPanel)
        fastTravelIdentifierPanel.repaint()
        editor.contentComponent.repaint()
    }

    private fun getFastTravelMappings(
        visibleText: String,
        visibleTextRange: TextRange,
    ): Map<String, Int> {
//        todo optimize splits and word length (make it even configurable?)
//        todo ignore folded areas
        val interestingIdentifiers = visibleText
            .split(' ', '.')
            .filter { it.isNotBlank() }
            .filter { it.length > MIN_WORD_LENGTH }
            .map { it.trim('\n') }
            .toSet()

        var identifierIndex = 0
        val mapping = interestingIdentifiers.map { identifier ->
            var index = visibleText.indexOf(identifier)
            val fastTravelers = mutableListOf<FastTravel>()

            while (index > 0) {
                val offset = visibleTextRange.startOffset + index
                if (identifierIndex < identifiers.size) {
                    fastTravelers += FastTravel(identifiers[identifierIndex], offset)
                }
                identifierIndex++
                index = visibleText.indexOf(identifier, startIndex = index + 1)
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
