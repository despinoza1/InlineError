package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.{HighlighterTargetArea, TextAttributes}
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiErrorElement, PsiFile}
import com.intellij.ui.components.JBLabel

import java.awt.Color
import scala.jdk.CollectionConverters._

class InlineErrorAction extends AnAction {

  def getErrors(psiFile: PsiFile): Seq[PsiErrorElement] = {
    if (psiFile != null) {
      return PsiTreeUtil.collectElementsOfType(psiFile, classOf[PsiErrorElement]).asScala.toSeq
    }
    Nil
  }

  def highlightError(editor: Editor, file: PsiFile): Unit = {
    if (editor == null) {
      return
    }

    val settings = InlineErrorState.getInstance().getState
    val document = file.getViewProvider.getDocument
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
      inlayModel
        .getAfterLineEndElementsInRange(0, document.getLineEndOffset(document.getLineCount - 1), classOf[ErrorLabel])
        .asScala
        .foreach(_.dispose())
    }

    if (!settings.isEnabled) return

    val errors = getErrors(file)
    if (errors.isEmpty) {
      return
    }

    val filteredErrors = errors
      .map(err => (document.getLineNumber(err.getTextOffset), err))
      .distinctBy(_._1)
      .map(_._2)

    for (error <- filteredErrors) {
      val highlighter = editor
        .getMarkupModel
        .addRangeHighlighter(error.getTextOffset, error.getTextOffset, 0, textAttribute, HighlighterTargetArea.LINES_IN_RANGE)
      highlighter.setGutterIconRenderer(new ErrorGutterRenderer(AllIcons.General.Error, error.getErrorDescription))

      val label = new JBLabel(error.getErrorDescription)
      val errorLabel = new ErrorLabel(label, new Color(settings.textColor))

      if (inlayModel != null) {
        inlayModel.addAfterLineEndElement(error.getTextOffset, true, errorLabel)
      }
    }
  }

  override def actionPerformed(e: AnActionEvent): Unit = {
    val project = e.getProject
    if (project == null || !project.isOpen) return

    val psiFile = e.getPresentation.getClientProperty("PsiFile")
    if (psiFile == null) return

    val editor = e.getData(CommonDataKeys.EDITOR)
    highlightError(editor, psiFile.asInstanceOf[PsiFile])
  }
}
