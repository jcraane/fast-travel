package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement


class FastTravelAction : AnAction() {
    private var fastTravelKeyListener: FastTravelKeyListener? = null

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT)
        val editor = actionEvent.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            return
        }

        fastTravelKeyListener?.removeFastTravelIdentifierPanel()
        fastTravelKeyListener = FastTravelKeyListener(editor).also {
            val showFastTravelIdentifiers = ShowFastTravelIdentifiers(editor, it)
            ApplicationManager.getApplication().runReadAction(showFastTravelIdentifiers)
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

    companion object {
        private val LOG = logger<FastTravelAction>()

        val numbers = (0..9).toList().map { it.toString() }
        val upperCase = ('A'..'Z').toList().map { it.toString() }
        val lowerCase = ('a'..'z').toList().map { it.toString() }
        val identifiers = numbers + lowerCase + upperCase
    }
}

