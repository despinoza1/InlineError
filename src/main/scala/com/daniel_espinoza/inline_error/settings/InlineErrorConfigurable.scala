package com.daniel_espinoza.inline_error.settings

import com.intellij.openapi.options.Configurable

import javax.swing._
import scala.language.implicitConversions

import SettingUtil.{color2Hex, hex2Color}

class InlineErrorConfigurable extends Configurable {
  private var inlineErrorsComponent: InlineErrorComponent = null

  override def getDisplayName: String = "Inline Error"

  override def getPreferredFocusedComponent: JComponent =
    inlineErrorsComponent.getPreferredFocusedComponent

  override def createComponent(): JComponent = {
    inlineErrorsComponent = new InlineErrorComponent()
    inlineErrorsComponent.getPanel
  }

  def createUIComponents(): JComponent = createComponent()

  override def isModified: Boolean = {
    val settings = InlineErrorState.getInstance()

    inlineErrorsComponent.getIsEnabled != settings.isEnabled
      .|(!color2Hex(inlineErrorsComponent.getBgColor).equals(settings.bgColor))
      .|(!color2Hex(inlineErrorsComponent.getTextColor).equals(settings.textColor))
  }

  override def apply(): Unit = {
    val settings = InlineErrorState.getInstance()

    settings.isEnabled = inlineErrorsComponent.getIsEnabled
    settings.bgColor = inlineErrorsComponent.getBgColor
    settings.textColor = inlineErrorsComponent.getTextColor
  }

  override def reset(): Unit = {
    val settings = InlineErrorState.getInstance()

    inlineErrorsComponent.setIsEnabled(settings.isEnabled)
    inlineErrorsComponent.setTextColor(settings.textColor)
    inlineErrorsComponent.setBgColor(settings.bgColor)
  }

  override def disposeUIResources(): Unit = inlineErrorsComponent = null
}
