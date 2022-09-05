package com.github.jcraane.fasttravel.configuration

import com.intellij.openapi.options.Configurable
import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.layout.panel
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JPanel

class FastTravelConfigurable : Configurable {
    private lateinit var settingsPanel: JPanel
    private var minWordLength: Int = FastTravelSettingsState.DEFAULT_MIN_WORD_LENGTH
    private val backgroundColorPanel: ColorPanel = ColorPanel()
    private val foregroundColorPanel: ColorPanel = ColorPanel()

    private val config: FastTravelSettingsState = FastTravelSettingsState.getInstance()

    override fun createComponent(): JComponent {
        settingsPanel = JPanel()
        return createFromConfig()
    }
    private fun createFromConfig(): JComponent {
        backgroundColorPanel.selectedColor = JBColor(config.background, config.background)
        foregroundColorPanel.selectedColor = JBColor(config.foreground, config.foreground)

        settingsPanel = panel {
            row {
                label(text = "Minimum wordt length")
                intTextField(
                    getter = {
                        config.minWordLength
                    },
                    setter = {
                        minWordLength = it
                    }
                )
            }

            row {
                label(text = "Background color")
                this.component(backgroundColorPanel)
            }

            row {
                label(text = "Foreground color")
                this.component(foregroundColorPanel)
            }
        }
        return settingsPanel
    }

    override fun isModified(): Boolean {
        var modified = false
        modified = modified || minWordLength != config.minWordLength
        modified = modified || backgroundColorPanel.selectedColor != Color(config.getBackgroundColor().rgb)
        modified = modified || foregroundColorPanel.selectedColor != Color(config.getForeGroundColor().rgb)
        return modified
    }

    override fun apply() {
        if (isModified.not()) {
            return
        }

        config.minWordLength = minWordLength
        config.background = backgroundColorPanel.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_BACKGROUND.rgb
        config.foreground = foregroundColorPanel.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_FOREGROUND.rgb
    }

    override fun getDisplayName(): String {
        return "FastTravel"
    }
}
