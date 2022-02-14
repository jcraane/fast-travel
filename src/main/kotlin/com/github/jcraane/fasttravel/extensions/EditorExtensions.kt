package com.github.jcraane.fasttravel.extensions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.TextRange
import java.awt.Point
import java.awt.Rectangle

fun Editor.getVisibleTextRange(): TextRange {
    val visibleArea: Rectangle = getScrollingModel().getVisibleArea()
    val startLogicalPosition: LogicalPosition = xyToLogicalPosition(visibleArea.location)
    val endVisualX = visibleArea.getX() + visibleArea.getWidth()
    val endVisualY = visibleArea.getY() + visibleArea.getHeight()
    val endLogicalPosition: LogicalPosition = xyToLogicalPosition(Point(endVisualX.toInt(), endVisualY.toInt()))
    return TextRange(logicalPositionToOffset(startLogicalPosition), logicalPositionToOffset(endLogicalPosition))
}
