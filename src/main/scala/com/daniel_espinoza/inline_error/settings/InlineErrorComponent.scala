package com.daniel_espinoza.inline_error.settings;

import com.intellij.ui.ColorPanel
import com.intellij.ui.components.{JBCheckBox, JBLabel}
import com.intellij.util.ui.FormBuilder

import java.awt._
import javax.swing._

class InlineErrorComponent {

  val inlineIsEnabled: JBCheckBox = new JBCheckBox("Enable inline errors?", true)
  val errorTextColor: ColorPanel = new ColorPanel()
  val highlightIsEnabled: JBCheckBox = new JBCheckBox("Enable highlight line?", false)
  val highlightColor: ColorPanel = new ColorPanel()

  highlightColor.setSelectedColor(null);
  errorTextColor.setSelectedColor(null);

  val highlightColorLabel = new JBLabel("Highlight color for error line");
  val errorTextColorLabel = new JBLabel("Color of error message");

  val rootPanel: JPanel = FormBuilder.createFormBuilder()
    .addComponent(inlineIsEnabled)
    .addLabeledComponent(errorTextColorLabel, errorTextColor)
    .addComponent(highlightIsEnabled)
    .addLabeledComponent(highlightColorLabel, highlightColor)
    .addComponentFillVertically(new JPanel(), 0)
    .getPanel

  def getPanel: JPanel = rootPanel

  def getPreferredFocusedComponent: JComponent = inlineIsEnabled

  def getIsEnabled: Boolean = inlineIsEnabled.isSelected
  def setIsEnabled(isEnabled: Boolean): Unit = inlineIsEnabled.setSelected(isEnabled)

  def getHighlightIsEnabled: Boolean = highlightIsEnabled.isSelected
  def setHighlightIsEnabled(isEnabled: Boolean): Unit = highlightIsEnabled.setSelected(isEnabled)

  def getHighlightColor: Color = highlightColor.getSelectedColor
  def setHighlightColor(color: Color): Unit = highlightColor.setSelectedColor(color)

  def getTextColor: Color = errorTextColor.getSelectedColor
  def setTextColor(color: Color): Unit = errorTextColor.setSelectedColor(color)
}
