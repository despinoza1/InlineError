package com.daniel_espinoza.inline_error

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.{GutterIconRenderer, TextAttributes}
import com.intellij.openapi.editor.{EditorCustomElementRenderer, Inlay}
import com.intellij.ui.components.JBLabel

import java.awt.{Color, Graphics, Rectangle}
import javax.swing.Icon


class ErrorLabel(label: JBLabel, textColor: Color, icon: Icon) extends EditorCustomElementRenderer {
  override def calcWidthInPixels(inlay: Inlay[_ <: EditorCustomElementRenderer]): Int = label.getPreferredSize.width

  override def calcHeightInPixels(inlay: Inlay[_ <: EditorCustomElementRenderer]): Int = label.getPreferredSize.height

  override def paint(inlay: Inlay[_ <: EditorCustomElementRenderer], g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes): Unit = {
    val editor = inlay.getEditor
    val colorScheme = editor.getColorsScheme

    val font = colorScheme.getFont(EditorFontType.PLAIN)

    g.setFont(font)
    g.setColor(textColor)
    g.drawString(label.getText, targetRegion.x, targetRegion.y + editor.getAscent)
  }

  override def calcGutterIconRenderer(inlay: Inlay[_ <: EditorCustomElementRenderer]): GutterIconRenderer = {
    new ErrorGutterRenderer(icon, label.getText)
  }
}
