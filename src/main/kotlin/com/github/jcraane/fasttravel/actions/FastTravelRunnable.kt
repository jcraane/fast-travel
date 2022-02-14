package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.editor.Editor

/**
 * Moves the caret to the actual offset of the fast travel identifier.2
 */
class FastTravelRunnable(
    private val editor: Editor,
    private val offset: Int,
) : Runnable {
    override fun run() {
        editor.caretModel.moveToOffset(offset)
    }
}
