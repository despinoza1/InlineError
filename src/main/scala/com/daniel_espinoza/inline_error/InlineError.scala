package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.{HighlighterTargetArea, TextAttributes}
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel

import java.awt.Color
import scala.jdk.CollectionConverters._

object InlineError {

  val logger: Logger = Logger.getInstance(InlineError.getClass)
  val PSIERROR: String = "PsiError"
  val PROBLEMS: String = "Problems"
  val COLLECTORS: Array[String] = Array(PROBLEMS, PSIERROR)

  def highlightError(problems: Seq[Error], project: Project): Unit = {
    val editor = FileEditorManager.getInstance(project).getSelectedTextEditor
    if (editor == null) return
    else if (editor.getEditorKind.name.toLowerCase != "main_editor") return

    val settings = InlineErrorState.getInstance.getState
    val document = editor.getDocument
    val inlayModel = editor.getInlayModel
    val colorScheme = editor.getColorsScheme
    val textAttribute = new TextAttributes(
      colorScheme.getDefaultForeground,
      if (settings.highlightIsEnabled) new Color(settings.highlightColor) else null,
      null,
      null,
      EditorFontType.PLAIN.ordinal)

    logger.debug("Disposing Gutter Icons and ErrorLabels")
    editor.getMarkupModel.getAllHighlighters.foreach(h => {
      if (h.getGutterIconRenderer != null && h.getGutterIconRenderer.isInstanceOf[ErrorGutterRenderer])
        editor.getMarkupModel.removeHighlighter(h)

      if (inlayModel != null && document.getLineCount > 0)
        inlayModel
          .getAfterLineEndElementsInRange(0, document.getLineEndOffset(document.getLineCount - 1), classOf[ErrorLabel])
          .asScala
          .foreach(_.dispose())
    })

    if (!settings.isEnabled) return
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

      val highlighter = editor
        .getMarkupModel
        .addRangeHighlighter(document.getLineStartOffset(error.line), document.getLineStartOffset(error.line), 0, textAttribute, HighlighterTargetArea.LINES_IN_RANGE)
      highlighter.setGutterIconRenderer(new ErrorGutterRenderer(AllIcons.General.Error, error.text))

      if (inlayModel != null) {
        val label = new JBLabel(error.text)
        val errorLabel = new ErrorLabel(label, new Color(settings.textColor))
        inlayModel.addAfterLineEndElement(document.getLineEndOffset(error.line), true, errorLabel)
      }
    }
  }

  case class Error(text: String, line: Int)
}
