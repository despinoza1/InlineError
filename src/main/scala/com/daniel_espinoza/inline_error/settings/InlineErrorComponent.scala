package com.daniel_espinoza.inline_error.settings

import com.daniel_espinoza.inline_error.InlineError
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.{JBLabel, JBPanel}
import com.intellij.ui.{ColorPanel, ContextHelpLabel}
import com.intellij.util.ui.FormBuilder

import java.awt._
import javax.swing._

class InlineErrorComponent {

  val SEVERITIES: Array[String] = Array("None", "ERROR", "WARN", "WEAK_WARN", "INFO")

  val inlineSeverity: JComboBox[String] = new ComboBox[String](SEVERITIES)
  val errorTextColor: ColorPanel = new ColorPanel()
  val warnTextColor: ColorPanel = new ColorPanel()
  val infoTextColor: ColorPanel = new ColorPanel()

  val highlightSeverity: JComboBox[String] = new ComboBox[String](SEVERITIES)
  val highlightError: ColorPanel = new ColorPanel()
  val highlightWarning: ColorPanel = new ColorPanel()
  val highlightInfo: ColorPanel = new ColorPanel()

  val nonErrorHelp: String = "<html>Only works with <strong>HighlightInfo</strong></html>"
  val listenerSelector: JComboBox[String] = new ComboBox[String](InlineError.COLLECTORS)
  val listenerHelp: String =
    """<html>
      |<strong>HighlightInfo</strong>: Allows to collect INFO, WARNING along with ERROR messages; <em>Recommended</em>
      |<br><strong>Problems</strong>: More features but not all languages support
      |<br><strong>PsiError</strong>: Faster but missing type checkers and linters
      |</html>""".stripMargin

  highlightError.setSelectedColor(null)
  highlightWarning.setSelectedColor(null)
  highlightInfo.setSelectedColor(null)
  errorTextColor.setSelectedColor(null)
  warnTextColor.setSelectedColor(null)
  infoTextColor.setSelectedColor(null)

  val severityLabel = new JBLabel("Severity for inline messages:")
  val highlightSeverityLabel = new JBLabel("Severity for lines for highlighting:")
  val highlightErrorLabel = new JBLabel("Highlight color for line with ERROR message:")
  val errorTextColorLabel = new JBLabel("Text color of ERROR message:")
  val highlightWarnLabel = new JBLabel("Highlight color for line with WARN message:")
  val warnTextColorLabel = new JBLabel("Text color of WARN message:")
  val highlightInfoLabel = new JBLabel("Highlight color for line with INFO message:")
  val infoTextColorLabel = new JBLabel("Text color of INFO message:")
  val listenerSelectorLabel = new JBLabel("Error Collector:")

  val rootPanel: JPanel = FormBuilder.createFormBuilder()
    .addLabeledComponent(severityLabel, inlineSeverity)
    .addLabeledComponent(getLabeledComponent(listenerSelectorLabel, listenerSelector, listenerHelp), new JPanel())
    .addLabeledComponent(errorTextColorLabel, errorTextColor)
    .addLabeledComponent(getLabeledComponent(warnTextColorLabel, warnTextColor, nonErrorHelp), new JPanel())
    .addLabeledComponent(getLabeledComponent(infoTextColorLabel, infoTextColor, nonErrorHelp), new JPanel())
    .addLabeledComponent(highlightSeverityLabel, highlightSeverity)
    .addLabeledComponent(highlightErrorLabel, highlightError)
    .addLabeledComponent(getLabeledComponent(highlightWarnLabel, highlightWarning, nonErrorHelp), new JPanel())
    .addLabeledComponent(getLabeledComponent(highlightInfoLabel, highlightInfo, nonErrorHelp), new JPanel())
    .addComponentFillVertically(new JPanel(), 0)
    .getPanel

  def getPanel: JPanel = rootPanel

  def getPreferredFocusedComponent: JComponent = inlineSeverity

  def getSeverity: String = inlineSeverity.getSelectedItem.toString

  def setSeverity(severity: String): Unit = inlineSeverity.setSelectedItem(severity)

  def getHighlightSeverity: String = highlightSeverity.getSelectedItem.toString

  def setHighlightSeverity(severity: String): Unit = highlightSeverity.setSelectedItem(severity)

  def getHighlightErrorColor: Color = highlightError.getSelectedColor

  def setHighlightErrorColor(color: Color): Unit = highlightError.setSelectedColor(color)

  def getErrorTextColor: Color = errorTextColor.getSelectedColor

  def setErrorTextColor(color: Color): Unit = errorTextColor.setSelectedColor(color)

  def getHighlightWarnColor: Color = highlightWarning.getSelectedColor

  def setHighlightWarnColor(color: Color): Unit = highlightWarning.setSelectedColor(color)

  def getWarnTextColor: Color = warnTextColor.getSelectedColor

  def setWarnTextColor(color: Color): Unit = warnTextColor.setSelectedColor(color)

  def getHighlightInfoColor: Color = highlightInfo.getSelectedColor

  def setHighlightInfoColor(color: Color): Unit = highlightInfo.setSelectedColor(color)

  def getInfoTextColor: Color = infoTextColor.getSelectedColor

  def setInfoTextColor(color: Color): Unit = infoTextColor.setSelectedColor(color)

  def getCollector: String = listenerSelector.getSelectedItem.toString

  def setCollector(collector: String): Unit = listenerSelector.setSelectedItem(collector)

  private def getLabeledComponent(label: JBLabel, component: JComponent, help: String): JBPanel[Nothing] = {
    val panel = new JBPanel
    val helpLabel: ContextHelpLabel = ContextHelpLabel.create(help)

    panel.add(label)
    panel.add(component)
    panel.add(helpLabel)

    panel
  }
}
