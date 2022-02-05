package com.github.jcraane.fasttravel.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.logger

class FastTravelAction : AnAction() {
    companion object {
        private val LOG = logger<FastTravelAction>()
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        println("Action performned now")
    }
}
