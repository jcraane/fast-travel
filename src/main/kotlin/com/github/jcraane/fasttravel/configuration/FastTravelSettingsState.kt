package com.github.jcraane.fasttravel.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import java.awt.Color

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "com.github.jcraane.fasttravel.configuration.FastTravelSettingsState",
    storages = [Storage("fastTravelConfig.xml")]
)
class FastTravelSettingsState : PersistentStateComponent<FastTravelSettingsState> {
    var minWordLength: Int = DEFAULT_MIN_WORD_LENGTH
    var background: Int = DEFAULT_BACKGROUND.rgb
    var foreground: Int = DEFAULT_FOREGROUND.rgb
    // When true, split words using camel case, so NavigationController is split in Navigation and Controller instead NavigationController.
    var useCamelHump: Boolean = false

    fun getBackgroundColor() = JBColor(background, background)
    fun getForeGroundColor() = JBColor(foreground, foreground)

    override fun getState(): FastTravelSettingsState {
        return this
    }

    override fun loadState(state: FastTravelSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val DEFAULT_BACKGROUND = JBColor(Color(32, 147, 227), Color(32, 147, 227))
        val DEFAULT_FOREGROUND = JBColor(Color(249, 255, 249), Color(232, 232, 225))
        const val DEFAULT_MIN_WORD_LENGTH = 5
        fun getInstance() = ServiceManager.getService(FastTravelSettingsState::class.java)
    }
}
