package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.icons.AllIcons
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorFontType
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
    case "WARN" => HighlightSeverity.WARNING
    case "ERROR" => HighlightSeverity.ERROR
    case _ => new HighlightSeverity("NONE", Int.MaxValue)
  }

  def filterSeverity(severity: HighlightSeverity): Boolean = {
    val settings = InlineErrorState.getInstance().getState
    severity.myVal >= stringToHighlightSeverity(settings.severity).myVal
  }

  def highlightError(problems: Seq[Error], project: Project): Unit = {
    val editor = FileEditorManager.getInstance(project).getSelectedTextEditor
    if (editor == null) return
    else if (editor.getEditorKind.name.toLowerCase != "main_editor") return

    val settings = InlineErrorState.getInstance.getState
    val document = editor.getDocument
    val inlayModel = editor.getInlayModel
    val colorScheme = editor.getColorsScheme

    editor.getMarkupModel.getAllHighlighters.foreach(h => {
      if (h.getGutterIconRenderer != null && h.getGutterIconRenderer.isInstanceOf[ErrorGutterRenderer])
        editor.getMarkupModel.removeHighlighter(h)

      if (inlayModel != null && document.getLineCount > 0)
        inlayModel
          .getAfterLineEndElementsInRange(0, document.getLineEndOffset(document.getLineCount - 1), classOf[ErrorLabel])
          .asScala
          .foreach(_.dispose())
    })

    if (settings.severity == "None") return
    if (problems.isEmpty) {
      return
    }

    val filteredErrors = problems
      .map(err => (err.line, err))
      .distinctBy(_._1)
      .filter(_._1 <= (document.getLineCount - 1))
      .filter(_._1 >= 0)
      .map(_._2)

    for (error <- filteredErrors) {
      logger.debug(s"Creating ErrorLabel for `[${error.line}]: ${error.text}`")

      val textAttribute = new TextAttributes(
        colorScheme.getDefaultForeground,
        error.getHighlightColor(settings),
        null,
        null,
        EditorFontType.PLAIN.ordinal)

      val highlighter = editor
        .getMarkupModel
        .addRangeHighlighter(document.getLineStartOffset(error.line), document.getLineStartOffset(error.line), 0, textAttribute, HighlighterTargetArea.LINES_IN_RANGE)
      highlighter.setGutterIconRenderer(new ErrorGutterRenderer(error.getIcon, error.text))

      if (inlayModel != null) {
        val label = new JBLabel(error.text)
        val errorLabel = new ErrorLabel(label, error.getTextColor(settings), error.getIcon)
        inlayModel.addAfterLineEndElement(document.getLineEndOffset(error.line), true, errorLabel)
      }
    }
  }

  case class Error(text: String, line: Int, severity: HighlightSeverity) {
    def getIcon: Icon = severity match {
      case HighlightSeverity.ERROR => AllIcons.General.Error
      case HighlightSeverity.WARNING => AllIcons.General.Warning
      case _ => AllIcons.General.Information
    }

    def getTextColor(settings: InlineErrorState): Color = severity match {
      case HighlightSeverity.ERROR => new Color(settings.errorTextColor)
      case HighlightSeverity.WARNING => new Color(settings.warnTextColor)
      case _ => new Color(settings.infoTextColor)
    }

    def getHighlightColor(settings: InlineErrorState): Color = {
      val color = severity match {
        case HighlightSeverity.ERROR => new Color(settings.highlightErrorColor)
        case HighlightSeverity.WARNING => new Color(settings.highlightWarnColor)
        case HighlightSeverity.INFORMATION => new Color(settings.highlightInfoColor)
        case _ => null
      }

      val sev = InlineError.stringToHighlightSeverity(settings.highlightSeverity)
      if (severity.myVal >= sev.myVal) color
      else null
    }

  }
}
