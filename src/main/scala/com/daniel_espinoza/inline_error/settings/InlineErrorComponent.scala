package com.daniel_espinoza.inline_error.settings

import com.daniel_espinoza.inline_error.InlineError
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.{JBCheckBox, JBLabel, JBPanel}
import com.intellij.ui.{ColorPanel, ContextHelpLabel}
import com.intellij.util.ui.FormBuilder

import java.awt._
import javax.swing._

class InlineErrorComponent {

  val inlineIsEnabled: JBCheckBox = new JBCheckBox("Enable inline errors?", true)
  val errorTextColor: ColorPanel = new ColorPanel()
  val highlightIsEnabled: JBCheckBox = new JBCheckBox("Enable highlighting line with error?", false)
  val highlightColor: ColorPanel = new ColorPanel()
  val listenerSelector: JComboBox[String] = new ComboBox[String](InlineError.COLLECTORS)
  val listenerHelp: ContextHelpLabel = ContextHelpLabel.create("<html><strong>Problems</strong>: More features but not all languages support; <em>Recommended<em><br><strong>PsiError</strong>: Faster but missing type checkers and linters; same as v0.0.3 of plugin</html>")

  highlightColor.setSelectedColor(null)
  errorTextColor.setSelectedColor(null)

  val highlightColorLabel = new JBLabel("Highlight color for line with error:")
  val errorTextColorLabel = new JBLabel("Text color of error message:")
  val listenerSelectorLabel = new JBLabel("Error Collector:")

  val selectorPanel = new JBPanel
  selectorPanel.add(listenerSelectorLabel)
  selectorPanel.add(listenerSelector)
  selectorPanel.add(listenerHelp)

  val rootPanel: JPanel = FormBuilder.createFormBuilder()
    .addComponent(inlineIsEnabled)
    .addLabeledComponent(errorTextColorLabel, errorTextColor)
    .addComponent(highlightIsEnabled)
    .addLabeledComponent(highlightColorLabel, highlightColor)
    .addLabeledComponent(selectorPanel, new JPanel())
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

  def getCollector: String = listenerSelector.getSelectedItem.toString
  def setCollector(collector: String): Unit = listenerSelector.setSelectedItem(collector)
}
