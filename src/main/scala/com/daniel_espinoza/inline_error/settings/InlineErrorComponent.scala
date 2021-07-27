package com.daniel_espinoza.inline_error.settings;

import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder

import javax.swing._
import java.awt._

class InlineErrorComponent {
  val DEFAULT_BACKGROUND_COLOR: Color = JBColor.RED
  val DEFAULT_TEXT_COLOR: Color = JBColor.RED

  val inlineIsEnabled: JBCheckBox = new JBCheckBox("Enable inline errors?", true)
  val errorBackgroundColor: ColorPanel = new ColorPanel()
  val errorTextColor: ColorPanel = new ColorPanel()

  errorBackgroundColor.setSelectedColor(DEFAULT_BACKGROUND_COLOR);
  errorTextColor.setSelectedColor(DEFAULT_TEXT_COLOR);

  val errorBackgroundColorLabel = new JBLabel("Background color for error line");
  val errorTextColorLabel = new JBLabel("Color of error message");

  val rootPanel: JPanel = FormBuilder.createFormBuilder()
    .addComponent(inlineIsEnabled)
    .addLabeledComponent(errorTextColorLabel, errorTextColor)
    .addLabeledComponent(errorBackgroundColorLabel, errorBackgroundColor)
    .getPanel

  def getPanel: JPanel = rootPanel

  def getPreferredFocusedComponent: JComponent = inlineIsEnabled

  def getIsEnabled: Boolean = inlineIsEnabled.isSelected

  def setIsEnabled(isEnabled: Boolean): Unit = inlineIsEnabled.setEnabled(isEnabled)

  def getBgColor: Color = errorBackgroundColor.getSelectedColor

  def setBgColor(color: Color): Unit = errorBackgroundColor.setSelectedColor(color)

  def getTextColor: Color = errorTextColor.getSelectedColor

  def setTextColor(color: Color): Unit = errorTextColor.setSelectedColor(color)
}
