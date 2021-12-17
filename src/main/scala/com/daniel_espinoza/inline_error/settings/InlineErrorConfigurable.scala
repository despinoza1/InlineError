package com.daniel_espinoza.inline_error.settings

import com.intellij.openapi.options.Configurable

import java.awt.Color
import javax.swing._

class InlineErrorConfigurable extends Configurable {
  private var inlineErrorsComponent: InlineErrorComponent = null

  override def getDisplayName: String = "Inline Error"

  override def getPreferredFocusedComponent: JComponent =
    inlineErrorsComponent.getPreferredFocusedComponent

  def createUIComponents(): JComponent = createComponent()

  override def createComponent(): JComponent = {
    inlineErrorsComponent = new InlineErrorComponent()
    inlineErrorsComponent.getPanel
  }

  override def isModified: Boolean = {
    val settings = InlineErrorState.getInstance()

    (inlineErrorsComponent.getIsEnabled != settings.isEnabled)
      .|(inlineErrorsComponent.getHighlightIsEnabled != settings.highlightIsEnabled)
      .|(inlineErrorsComponent.getHighlightColor.getRGB != settings.highlightColor)
      .|(inlineErrorsComponent.getTextColor.getRGB != settings.textColor)
      .|(inlineErrorsComponent.getCollector != settings.collector)
  }

  override def apply(): Unit = {
    val settings = InlineErrorState.getInstance()

    settings.isEnabled = inlineErrorsComponent.getIsEnabled
    settings.highlightIsEnabled = inlineErrorsComponent.getHighlightIsEnabled
    settings.highlightColor = inlineErrorsComponent.getHighlightColor.getRGB
    settings.textColor = inlineErrorsComponent.getTextColor.getRGB
    settings.collector = inlineErrorsComponent.getCollector
  }

  override def reset(): Unit = {
    val settings = InlineErrorState.getInstance()

    inlineErrorsComponent.setIsEnabled(settings.isEnabled)
    inlineErrorsComponent.setHighlightIsEnabled(settings.highlightIsEnabled)
    inlineErrorsComponent.setTextColor(new Color(settings.textColor))
    inlineErrorsComponent.setHighlightColor(new Color(settings.highlightColor))
    inlineErrorsComponent.setCollector(settings.collector)
  }

  override def disposeUIResources(): Unit = inlineErrorsComponent = null
}
