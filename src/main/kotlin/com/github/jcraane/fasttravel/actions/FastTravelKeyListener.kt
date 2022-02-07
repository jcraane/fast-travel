package com.github.jcraane.fasttravel.actions

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class FastTravelKeyListener : KeyListener {
    override fun keyTyped(e: KeyEvent?) {
        e?.consume()

    }

    override fun keyPressed(e: KeyEvent?) {
        /*if (KeyEvent.VK_ESCAPE == e?.getKeyChar()?.code) {
        }*/
    }

    override fun keyReleased(e: KeyEvent?) {
        // Do nothing
    }
}
