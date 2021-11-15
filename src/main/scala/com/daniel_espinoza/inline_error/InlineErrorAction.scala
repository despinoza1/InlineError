package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.analysis.problemsView.toolWindow.HighlightingProblem
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.{HighlighterTargetArea, TextAttributes}
import com.intellij.ui.components.JBLabel

import java.awt.Color
import scala.jdk.CollectionConverters._

class InlineErrorAction extends AnAction {
  import InlineErrorAction._

  def highlightError(editor: Editor, problems: List[HighlightingProblem]): Unit = {
    if (editor == null) return
    else if (editor.getEditorKind.name.toLowerCase != "main_editor") return

    val settings = InlineErrorState.getInstance().getState
    val document = editor.getDocument
    val inlayModel = editor.getInlayModel
    val colorScheme = editor.getColorsScheme
    val textAttribute = new TextAttributes(
      colorScheme.getDefaultForeground,
      if (settings.highlightIsEnabled) new Color(settings.highlightColor) else null,
      null,
      null,
      EditorFontType.PLAIN.ordinal)

    editor.getMarkupModel.removeAllHighlighters()
    if (inlayModel != null && document.getLineCount > 0) {
      logger.debug("Disposing ErrorLabels")
      inlayModel
        .getAfterLineEndElementsInRange(0, document.getLineEndOffset(document.getLineCount - 1), classOf[ErrorLabel])
        .asScala
        .foreach(_.dispose())
    }

    if (!settings.isEnabled) return
    if (problems.isEmpty) {
      return
    }

    val filteredErrors = problems
      .map(err => (err.getLine, err))
      .distinctBy(_._1)
      .filter(_._1 <= (document.getLineCount - 1))
      .map(_._2)

    for (error <- filteredErrors) {
      logger.debug(s"Creating Label for error [${error.getLine}]: ${error.getText}")

      val highlighter = editor
        .getMarkupModel
        .addRangeHighlighter(document.getLineStartOffset(error.getLine), document.getLineStartOffset(error.getLine), 0, textAttribute, HighlighterTargetArea.LINES_IN_RANGE)
      highlighter.setGutterIconRenderer(new ErrorGutterRenderer(AllIcons.General.Error, error.getText))

      val label = new JBLabel(error.getText)
      val errorLabel = new ErrorLabel(label, new Color(settings.textColor))

      if (inlayModel != null) {
        inlayModel.addAfterLineEndElement(document.getLineEndOffset(error.getLine), true, errorLabel)
      }
    }
  }

  override def actionPerformed(e: AnActionEvent): Unit = {
    val project = e.getProject
    if (project == null || !project.isOpen) return

    val problems = e.getPresentation.getClientProperty(InlineErrorKey.key)
    if (problems == null) return

    val editor = e.getData(CommonDataKeys.EDITOR)
    highlightError(editor, problems)
  }
}

object InlineErrorAction {
  val logger: Logger = Logger.getInstance(classOf[InlineErrorAction])
}
