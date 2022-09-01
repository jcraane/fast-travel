package com.github.jcraane.fasttravel.actions

import com.github.jcraane.fasttravel.renderer.FastTravelIdentifierPanel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class FastTravelKeyListener(
    private val editor: Editor,
    private val fastTravelAction: FastTravelAction,
) : KeyListener {

    var fastTravelIdentifierPanel: FastTravelIdentifierPanel? = null
    var fastTravelMapping: Map<String, Int>? = null

    private var numberOfKeyPresses = 0
    private var pressedIdentifier = ""

    override fun keyTyped(e: KeyEvent) {
        e.consume()
        if (numberOfKeyPresses == 0) {
            pressedIdentifier += e.keyChar
            numberOfKeyPresses++
        } else {
            pressedIdentifier += e.keyChar
            fastTravelToIdentifier(pressedIdentifier)
            removeFastTravelIdentifierPanel()
            fastTravelAction.restoreKeyListeners()
        }
    }

    private fun fastTravelToIdentifier(identifier: String) {
        val offset = fastTravelMapping?.get(pressedIdentifier)
        if (offset != null) {
            ApplicationManager.getApplication().runReadAction(FastTravelRunnable(editor, offset))
        }
    }

    @OptIn(kotlin.ExperimentalStdlibApi::class)
    override fun keyPressed(e: KeyEvent) {
        if (KeyEvent.VK_ESCAPE == e.keyChar.code) {
            removeFastTravelIdentifierPanel()
            fastTravelAction.restoreKeyListeners()
        }
    }

    @OptIn(kotlin.ExperimentalStdlibApi::class)
    fun removeFastTravelIdentifierPanel() {
        numberOfKeyPresses = 0
        pressedIdentifier = ""
        editor.contentComponent.removeKeyListener(this)
        val parent = fastTravelIdentifierPanel?.parent
        parent?.remove(fastTravelIdentifierPanel)
        parent?.repaint()
    }

    override fun keyReleased(e: KeyEvent?) {
        // Do nothing
    }
}
