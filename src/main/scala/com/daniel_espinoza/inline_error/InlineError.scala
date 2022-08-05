package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.icons.AllIcons
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.{HighlighterTargetArea, TextAttributes}
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel

import java.awt.Color
import javax.swing.Icon
import scala.jdk.CollectionConverters._

object InlineError {

  val logger: Logger = Logger.getInstance(InlineError.getClass)
  val PSIERROR: String = "PsiError"
  val PROBLEMS: String = "Problems"
  val HIGHLIGHT: String = "HighlightInfo"
  val COLLECTORS: Array[String] = Array(PROBLEMS, PSIERROR, HIGHLIGHT)

  def stringToHighlightSeverity(severity: String): HighlightSeverity = severity match {
    case "INFO" => HighlightSeverity.INFORMATION
    case "WEAK_WARN" => HighlightSeverity.WEAK_WARNING
    case "WARN" => HighlightSeverity.WARNING
    case "ERROR" => HighlightSeverity.ERROR
    case _ => new HighlightSeverity("NONE", Int.MaxValue)
  }

  def filterSeverity(severity: HighlightSeverity): Boolean = {
    val settings = InlineErrorState.getInstance().getState
    severity.myVal >= stringToHighlightSeverity(settings.severity).myVal
  }

  def getEditor(project: Project): Option[Editor] = {
    val editor = FileEditorManager.getInstance(project).getSelectedTextEditor

    if (editor == null) None
    else if (editor.getEditorKind.name.toLowerCase != "main_editor") None
    else Some(editor)
  }

  def clearHighlighters(editor: Editor): Unit =
    editor.getMarkupModel.getAllHighlighters.foreach(h => {
      if (h.getGutterIconRenderer != null && h.getGutterIconRenderer.isInstanceOf[ErrorGutterRenderer])
        editor.getMarkupModel.removeHighlighter(h)

      if (editor.getInlayModel != null && editor.getDocument.getLineCount > 0)
        editor.getInlayModel
          .getAfterLineEndElementsInRange(0, editor.getDocument.getLineEndOffset(editor.getDocument.getLineCount - 1), classOf[ErrorLabel])
          .asScala
          .foreach(_.dispose())
    })

  def createErrorLabel(editor: Editor, settings: InlineErrorState)(error: Error): Unit = {
    logger.debug(s"Creating ErrorLabel for `[${error.line}]: ${error.text}`")
    val document = editor.getDocument
    val inlayModel = editor.getInlayModel
    val colorScheme = editor.getColorsScheme

    val textAttribute = new TextAttributes(
      colorScheme.getDefaultForeground,
      error.getHighlightColor(settings),
      null,
      null,
      EditorFontType.PLAIN.ordinal)

    val highlighter = editor
      .getMarkupModel
      .addRangeHighlighter(document.getLineStartOffset(error.line), document.getLineEndOffset(error.line), 0, textAttribute, HighlighterTargetArea.LINES_IN_RANGE)
    highlighter.setGutterIconRenderer(new ErrorGutterRenderer(error.getIcon, error.text))

    if (inlayModel != null) {
      val label = new JBLabel(error.text)
      val errorLabel = new ErrorLabel(label, error.getTextColor(settings), error.getIcon)
      inlayModel.addAfterLineEndElement(document.getLineEndOffset(error.line), true, errorLabel)
    }
  }
  def makeHighlightersInline(project: Project): Unit = {
    val editor = getEditor(project)
    if (editor.isEmpty) return

    clearHighlighters(editor.get)

    val settings = InlineErrorState.getInstance.getState
    if (settings == null || settings.severity == "None") return
    val document = editor.get.getDocument

    val highlighters = DocumentMarkupModel.forDocument(document, project, false).getAllHighlighters

    highlighters
      .filter(_.getErrorStripeTooltip.isInstanceOf[HighlightInfo])
      .map(_.getErrorStripeTooltip.asInstanceOf[HighlightInfo])
      .map(h => Error(h.getDescription, document.getLineNumber(h.getEndOffset), h.getSeverity))
      .filter(e => filterSeverity(e.severity) && e.text != null && e.text.nonEmpty && e.line <= (document.getLineCount - 1) && e.line >= 0)
      .map(err => (err.line, err))
      .sortWith(_._2.severity.myVal > _._2.severity.myVal)
      .distinctBy(_._1)
      .map(_._2)
      .foreach(createErrorLabel(editor.get, settings))
  }
  def highlightErrorSeq(problems: Seq[Error], project: Project): Unit = {
    val editor = getEditor(project)
    if (editor.isEmpty) return

    clearHighlighters(editor.get)

    val settings = InlineErrorState.getInstance.getState
    if (settings == null || settings.severity == "None") return
    val document = editor.get.getDocument

    problems
      .map(err => (err.line, err))
      .distinctBy(_._1)
      .filter(_._1 <= (document.getLineCount - 1))
      .filter(_._1 >= 0)
      .map(_._2)
      .foreach(createErrorLabel(editor.get, settings))
  }

  case class Error(text: String, line: Int, severity: HighlightSeverity) {
    def getIcon: Icon = severity match {
      case HighlightSeverity.ERROR => AllIcons.General.Error
      case HighlightSeverity.WARNING => AllIcons.General.Warning
      case HighlightSeverity.WEAK_WARNING => AllIcons.General.Warning
      case _ => AllIcons.General.Information
    }

    def getTextColor(settings: InlineErrorState): Color = severity match {
      case HighlightSeverity.ERROR => new Color(settings.errorTextColor)
      case HighlightSeverity.WARNING => new Color(settings.warnTextColor)
      case HighlightSeverity.WEAK_WARNING => new Color(settings.warnTextColor)
      case _ => new Color(settings.infoTextColor)
    }

    def getHighlightColor(settings: InlineErrorState): Color = {
      val color = severity match {
        case HighlightSeverity.ERROR => new Color(settings.highlightErrorColor)
        case HighlightSeverity.WARNING => new Color(settings.highlightWarnColor)
        case HighlightSeverity.WEAK_WARNING => new Color(settings.highlightWarnColor)
        case HighlightSeverity.INFORMATION => new Color(settings.highlightInfoColor)
        case _ => null
      }

      val sev = InlineError.stringToHighlightSeverity(settings.highlightSeverity)
      if (severity.myVal >= sev.myVal) color
      else null
    }
  }
}
