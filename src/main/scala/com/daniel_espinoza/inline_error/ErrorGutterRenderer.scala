package com.daniel_espinoza.inline_error

import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware

import javax.swing.Icon

class ErrorGutterRenderer(icon: Icon, text: String) extends GutterIconRenderer with DumbAware {
  override def getIcon: Icon = icon

  override def getTooltipText: String = text

  override def getAlignment: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT

  override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (obj == null) return false
    if (this.getClass != obj.getClass) return false

    val other = obj.asInstanceOf[GutterIconRenderer]
    this.getIcon == other.getIcon
  }

  override def hashCode: Int = getIcon.hashCode()
}
