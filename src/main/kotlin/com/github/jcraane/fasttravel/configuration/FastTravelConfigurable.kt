package com.github.jcraane.fasttravel.configuration

import com.intellij.openapi.options.Configurable
import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JPanel

class FastTravelConfigurable : Configurable {
    private lateinit var settingsPanel: JPanel
    private var minWordLength = JBTextField()
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
        minWordLength.text = config.minWordLength.toString()

        settingsPanel = panel {
            row {
                label(text = "Minimum word length")
                component(minWordLength)
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
        val wordLength = minWordLength.text
        if (wordLength.isNotBlank()) {
            modified = modified || wordLength.toInt() != config.minWordLength
        }
        modified = modified || backgroundColorPanel.selectedColor != Color(config.getBackgroundColor().rgb)
        modified = modified || foregroundColorPanel.selectedColor != Color(config.getForeGroundColor().rgb)
        return modified
    }

    override fun apply() {
        if (isModified.not()) {
            return
        }

        config.minWordLength = minWordLength.text.toInt()
        config.background = backgroundColorPanel.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_BACKGROUND.rgb
        config.foreground = foregroundColorPanel.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_FOREGROUND.rgb
    }

    override fun getDisplayName(): String {
        return "FastTravel"
    }
}
