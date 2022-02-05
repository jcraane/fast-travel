package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.util.elementType
import java.awt.Color
import java.awt.Font
import javax.swing.SwingUtilities


class FastTravelAction : AnAction() {
    private var previousHandler: TypedActionHandler? = null

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT)
        val editor = actionEvent.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            return
        }

        val elements = mutableSetOf<PsiElement>()
        val psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE)
        psiFile?.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                val type = element.elementType.toString()
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
//                    else -> println("$type = ${element.text}")
                }
            }
        })

        val mapping = mapElementsToIdentifiers(elements)
        setupFastTravel(mapping)
        val graphics = editor.component.graphics
        graphics.color = Color.YELLOW
        mapping.forEach { entry ->
            val point = editor.visualPositionToXY(editor.offsetToVisualPosition(entry.value.textOffset));
            SwingUtilities.invokeLater {
                graphics.drawString(entry.key.toString(), point.x, point.y)
            }
        }
    }

    private fun mapElementsToIdentifiers(elements: Set<PsiElement>): Map<Char, PsiElement> {
        val mapping = mutableMapOf<Char, PsiElement>()
        elements.forEachIndexed { index, psiElement ->
            if (index < identifiers.size) {
                mapping[identifiers[index][0]] = psiElement
            }
        }
        return mapping
    }

    private fun setupFastTravel(mapping: Map<Char, PsiElement>) {
        val actionManager = EditorActionManager.getInstance()
        val typedAction = actionManager.typedAction
        if (previousHandler == null) {
            previousHandler = actionManager.typedAction.handler
        }

        previousHandler?.let {
            typedAction.setupRawHandler(QuickJumpHandler(mapping.toMap(), it))
        }
    }

    companion object {
        private val LOG = logger<FastTravelAction>()

        val numbers = (0..9).toList().map { it.toString() }
        val upperCase = ('A'..'Z').toList().map { it.toString() }
        val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = numbers + lowerCase + upperCase
    }
}
