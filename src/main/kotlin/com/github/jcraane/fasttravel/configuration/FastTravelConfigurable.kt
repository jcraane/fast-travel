package com.github.jcraane.fasttravel.configuration

import com.intellij.openapi.options.Configurable
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.panels.HorizontalBox
import com.intellij.ui.components.panels.VerticalBox
import com.intellij.util.containers.concat
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class FastTravelConfigurable : Configurable {
    private lateinit var settingsPanel: JPanel
    private val minWordLength: JTextField = JTextField()
    private val backgroundColor: ColorPanel = ColorPanel()
    private val foregroundColor: ColorPanel = ColorPanel()

    private val config: FastTravelSettingsState = FastTravelSettingsState.getInstance()

    override fun createComponent(): JComponent {
        settingsPanel = JPanel()
        return createFromConfig()
    }

    private fun createFromConfig(): JComponent {
        settingsPanel = JPanel()
        val root = VerticalBox()

        val minWordLengthHolder = HorizontalBox()
        minWordLengthHolder.add(JLabel().apply {
            text = "Minimum word length"
        })
        minWordLengthHolder.add(minWordLengthHolder)
        root.add(minWordLengthHolder)
        settingsPanel.add(root)
        return settingsPanel
    }

    override fun isModified(): Boolean {
        var modified = false
        val minWords = minWordLength.text
        if (minWords.isNotBlank()) {
            modified = modified || minWordLength.text.toInt() != config.minWordLength
        }
        modified = modified || backgroundColor.selectedColor != Color(config.getBackgroundColor().rgb)
        modified = modified || foregroundColor.selectedColor != Color(config.getForeGroundColor().rgb)
        return modified
    }

    override fun apply() {
        if (isModified.not()) {
            return
        }

        config.minWordLength = minWordLength.text.toInt()
        config.background = backgroundColor.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_BACKGROUND.rgb
        config.foreground = foregroundColor.selectedColor?.rgb ?: FastTravelSettingsState.DEFAULT_FOREGROUND.rgb
    }

    override fun getDisplayName(): String {
        return "FastTravel"
    }
}
