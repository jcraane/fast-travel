package com.github.jcraane.fasttravel.renderer

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.ui.JBColor
import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.JComponent

class FastTravelIdentifierPanel(
    private val editor: Editor,
    private val fastTravelers: Map<String, Int>,
    private val foregroundColor: JBColor,
    private val backgroundColor: JBColor,
) : JComponent() {

    init {
        this.setLocation(0, 0)
        val visibleArea = editor.scrollingModel.visibleArea
        val parent = editor.contentComponent
        val x = (parent.location.getX() + visibleArea.getX() + editor.scrollingModel.horizontalScrollOffset).toInt()
        this.setBounds(x, visibleArea.getY().toInt(), visibleArea.getWidth().toInt(), visibleArea.getHeight().toInt())
    }

    override fun paint(graphics: Graphics) {
        val font: Font = editor.colorsScheme.getFont(EditorFontType.PLAIN)
        val fontMetrics: FontMetrics = editor.contentComponent.getFontMetrics(font)
        graphics.font = font

        fastTravelers.forEach { entry ->
            val offset = entry.value
            val fontRect = fontMetrics.getStringBounds(entry.key, graphics)
            drawBackground(graphics, getX(offset), getY(offset), backgroundColor, fontRect)
            drawMarkerChar(graphics, getX(offset), getY(offset) + font.size * 1.2, entry.key, foregroundColor)
        }

        super.paint(graphics)
    }

    private fun getY(offset: Int): Double {
        val parentLocation: Point = editor.contentComponent.location
        return getVisiblePosition(offset).getY() + parentLocation.getY()
    }

    private fun getX(offset: Int): Double {
        val parentLocation: Point = editor.contentComponent.location
        return getVisiblePosition(offset).getX() + parentLocation.getX()
    }

    private fun getVisiblePosition(offset: Int): Point {
        return editor.visualPositionToXY(editor.offsetToVisualPosition(offset))
    }

    private fun drawMarkerChar(g: Graphics, x: Double, y: Double, fastTravelChar: String, firstJumpForeground: Color) {
        g.color = firstJumpForeground
        (g as Graphics2D).drawString(fastTravelChar, x.toFloat(), y.toFloat())
    }

    private fun drawBackground(g: Graphics, x: Double, y: Double, firstJumpBackground: Color, fontRect: Rectangle2D) {
        g.color = firstJumpBackground
        g.fillRect(x.toInt(), y.toInt(), (fontRect.width * 1.02).toInt(), (fontRect.height * 1.08).toInt())
    }
}
