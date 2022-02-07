package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.extensions.within
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.util.elementType
import java.awt.Color

class ShowFastTravelIdentifiers(private val editor: Editor, private val psiFile: PsiFile) : Runnable {
    override fun run() {
        val elements = mutableSetOf<PsiElement>()

//        todo add keylistener for the fast travel actions

        val lines = editor.document.text.split("\n")
        val text = editor.document.text
        val previousFoldingRange: IntRange? = null
        val verticalScrollOffset = editor.scrollingModel.verticalScrollOffset
        val nonFoldedVisibleText = getNonFoldedVisualLines(previousFoldingRange, lines, text, verticalScrollOffset)

        println(nonFoldedVisibleText.joinToString("\n"))
//        extractPsiElementsForFastTravel(elements)
    }

    private fun getNonFoldedVisualLines(
        previousFoldingRange: IntRange?,
        lines: List<String>,
        text: @NlsSafe String,
        verticalScrollOffset: Int
    ): List<String> {
        var previousFoldingRange1 = previousFoldingRange
        val nonFoldedVisibleText = editor.foldingModel.allFoldRegions.mapNotNull { foldingRegion ->
            val pRange = previousFoldingRange1
            val range = foldingRegion.startOffset until foldingRegion.endOffset
            val lineNumberOfStartOfFold = editor.offsetToLogicalPosition(foldingRegion.startOffset).line

            val startTextOfFoldingRegion = lines[lineNumberOfStartOfFold]
            val linesInFoldingRegion = text.substring(range).split("\n").toMutableList()

            val result = if (editor.visualLineToY(lineNumberOfStartOfFold) < verticalScrollOffset) {
                null
            } else if (foldingRegion.isExpanded
                && pRange == null
                || (foldingRegion.isExpanded && pRange != null && !range.within(pRange))
            ) {
                linesInFoldingRegion[0] = startTextOfFoldingRegion
                linesInFoldingRegion
            } else {
                null
            }

            previousFoldingRange1 = range
            result
        }.flatten()
        return nonFoldedVisibleText
    }

    private fun extractPsiElementsForFastTravel(elements: MutableSet<PsiElement>) {
        psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                val type = element.elementType.toString()
                if (element.text.length > MIN_LENGTH) {
                    when (type) {
                        "IDENTIFIER" -> {
                            elements += element
                        }
                        "VALUE_ARGUMENT" -> {
                            elements += element
                        }
                        "REGULAR_STRING_PART" -> {
                            elements += element
                        }
                        else -> println("$type = ${element.text}")
                    }
                }
            }
        })

        val mapping = mapElementsToIdentifiers(elements)
        val graphics = editor.component.graphics
        graphics.color = Color.YELLOW
        mapping.forEach { entry ->
            val point = editor.visualPositionToXY(editor.offsetToVisualPosition(entry.value.textOffset));
            ApplicationManager.getApplication().invokeLater({
                graphics.drawString(entry.key.toString(), point.x, point.y)
            }, ModalityState.stateForComponent(editor.component))
        }
    }

    private fun mapElementsToIdentifiers(elements: Set<PsiElement>): Map<Char, PsiElement> {
        val mapping = mutableMapOf<Char, PsiElement>()
        elements.forEachIndexed { index, psiElement ->
            if (index < FastTravelAction.identifiers.size) {
                mapping[FastTravelAction.identifiers[index][0]] = psiElement
            }
        }
        return mapping
    }

    companion object {
        private val LOG = logger<FastTravelAction>()

        val numbers = (0..9).toList().map { it.toString() }
        val upperCase = ('A'..'Z').toList().map { it.toString() }
        val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = numbers + lowerCase + upperCase
        private const val MIN_LENGTH = 7
    }
}
