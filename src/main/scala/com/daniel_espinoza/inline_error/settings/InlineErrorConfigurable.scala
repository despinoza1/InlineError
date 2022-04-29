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

    (inlineErrorsComponent.getSeverity != settings.severity)
      .|(inlineErrorsComponent.getHighlightSeverity != settings.highlightSeverity)
      .|(inlineErrorsComponent.getHighlightErrorColor.getRGB != settings.highlightErrorColor)
      .|(inlineErrorsComponent.getErrorTextColor.getRGB != settings.errorTextColor)
      .|(inlineErrorsComponent.getHighlightWarnColor.getRGB != settings.highlightWarnColor)
      .|(inlineErrorsComponent.getWarnTextColor.getRGB != settings.warnTextColor)
      .|(inlineErrorsComponent.getHighlightInfoColor.getRGB != settings.highlightInfoColor)
      .|(inlineErrorsComponent.getInfoTextColor.getRGB != settings.infoTextColor)
      .|(inlineErrorsComponent.getCollector != settings.collector)
  }

  override def apply(): Unit = {
    val settings = InlineErrorState.getInstance()

    settings.severity = inlineErrorsComponent.getSeverity
    settings.highlightSeverity = inlineErrorsComponent.getHighlightSeverity
    settings.highlightErrorColor = inlineErrorsComponent.getHighlightErrorColor.getRGB
    settings.errorTextColor = inlineErrorsComponent.getErrorTextColor.getRGB
    settings.warnTextColor = inlineErrorsComponent.getWarnTextColor.getRGB
    settings.highlightWarnColor = inlineErrorsComponent.getHighlightWarnColor.getRGB
    settings.infoTextColor = inlineErrorsComponent.getInfoTextColor.getRGB
    settings.highlightInfoColor = inlineErrorsComponent.getHighlightInfoColor.getRGB
    settings.collector = inlineErrorsComponent.getCollector
  }

  override def reset(): Unit = {
    val settings = InlineErrorState.getInstance()

    inlineErrorsComponent.setSeverity(settings.severity)
    inlineErrorsComponent.setHighlightSeverity(settings.highlightSeverity)
    inlineErrorsComponent.setErrorTextColor(new Color(settings.errorTextColor))
    inlineErrorsComponent.setHighlightErrorColor(new Color(settings.highlightErrorColor))
    inlineErrorsComponent.setWarnTextColor(new Color(settings.warnTextColor))
    inlineErrorsComponent.setHighlightWarnColor(new Color(settings.highlightWarnColor))
    inlineErrorsComponent.setInfoTextColor(new Color(settings.infoTextColor))
    inlineErrorsComponent.setHighlightInfoColor(new Color(settings.highlightInfoColor))
    inlineErrorsComponent.setCollector(settings.collector)
  }

  override def disposeUIResources(): Unit = inlineErrorsComponent = null
}
