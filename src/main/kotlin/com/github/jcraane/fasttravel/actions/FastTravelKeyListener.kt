package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.renderer.FastTravelIdentifierPanel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class FastTravelKeyListener(
    private val editor: Editor,
) : KeyListener {

    var fastTravelIdentifierPanel: FastTravelIdentifierPanel? = null
    var fastTravelMapping: Map<String, Int>? = null

    override fun keyTyped(e: KeyEvent) {
        e.consume()
        fastTravelToIdentifier(e.keyChar)
        removeFastTravelIdentifierPanel()
    }

    private fun fastTravelToIdentifier(keyChar: Char) {
        val offset = fastTravelMapping?.get(keyChar.toString())
        if (offset != null) {
            ApplicationManager.getApplication().runReadAction(FastTravelRunnable(editor, offset))
        }
    }

    @OptIn(kotlin.ExperimentalStdlibApi::class)
    override fun keyPressed(e: KeyEvent) {
        if (KeyEvent.VK_ESCAPE == e.keyChar.code) {
            removeFastTravelIdentifierPanel()
        }
    }

    @OptIn(kotlin.ExperimentalStdlibApi::class)
    fun removeFastTravelIdentifierPanel() {
        editor.contentComponent.removeKeyListener(this)
        val parent = fastTravelIdentifierPanel?.parent
        parent?.remove(fastTravelIdentifierPanel)
        parent?.repaint()
    }

    override fun keyReleased(e: KeyEvent?) {
        // Do nothing
    }
}
