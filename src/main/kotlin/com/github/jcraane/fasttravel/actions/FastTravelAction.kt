package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import java.awt.event.KeyListener


class FastTravelAction : AnAction() {
    private var fastTravelKeyListener: FastTravelKeyListener? = null
    private var keyListeners: Array<KeyListener>? = null
    private var editor: Editor? = null

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val project = actionEvent.getData(CommonDataKeys.PROJECT)
        val editor = actionEvent.getData(CommonDataKeys.EDITOR)
        if (project == null || editor == null) {
            return
        }

        this.editor = editor
        keyListeners = removeExistingKeyListeners(editor)
        fastTravelKeyListener?.removeFastTravelIdentifierPanel()
        fastTravelKeyListener = FastTravelKeyListener(editor, this).also {
            val showFastTravelIdentifiers = ShowFastTravelIdentifiers(editor, it)
            ApplicationManager.getApplication().runReadAction(showFastTravelIdentifiers)
        }
    }

    private fun removeExistingKeyListeners(editor: Editor): Array<KeyListener> {
        val keyListeners = editor.contentComponent.keyListeners
        keyListeners?.forEach { keyListener ->
            editor.contentComponent.removeKeyListener(keyListener)
        }
        return keyListeners
    }

    fun restoreKeyListeners() {
        keyListeners?.forEach { keyListener ->
            editor?.contentComponent?.addKeyListener(keyListener)
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

