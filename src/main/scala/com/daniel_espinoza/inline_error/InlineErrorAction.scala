package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.{GutterIconRenderer, HighlighterTargetArea, TextAttributes}
import com.intellij.openapi.editor.{Editor, EditorCustomElementRenderer, Inlay}
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiErrorElement, PsiFile}
import com.intellij.ui.components.JBLabel

import java.awt.{Color, Graphics, Rectangle}
import javax.swing.Icon
import scala.jdk.CollectionConverters._

class InlineErrorAction extends AnAction {
  private class ErrorGutterRenderer(icon: Icon, text: String) extends GutterIconRenderer with DumbAware {
    override def getIcon: Icon = icon

    override def getTooltipText: String = text

    override def getAlignment: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT

    override def equals(obj: Any): Boolean = {
      if (this == obj) return true
      if (obj == null) return false
      if (this.getClass != obj.getClass) return false

      val other = obj.asInstanceOf[GutterIconRenderer]
      this.getIcon == other.getIcon
    }

    override def hashCode: Int = getIcon.hashCode()
  }

  private class ErrorLabel(label: JBLabel, textColor: Color) extends EditorCustomElementRenderer {
    override def calcWidthInPixels(inlay: Inlay[_ <: EditorCustomElementRenderer]): Int = label.getPreferredSize.width

    override def calcHeightInPixels(inlay: Inlay[_ <: EditorCustomElementRenderer]): Int = label.getPreferredSize.height

    override def paint(inlay: Inlay[_ <: EditorCustomElementRenderer], g: Graphics, targetRegion: Rectangle, textAttributes: TextAttributes): Unit = {
      val editor = inlay.getEditor
      val colorScheme = editor.getColorsScheme

      val font = colorScheme.getFont(EditorFontType.PLAIN)

      g.setFont(font)
      g.setColor(textColor)
      g.drawString(label.getText, targetRegion.x, targetRegion.y + editor.getAscent)
    }

    override def calcGutterIconRenderer(inlay: Inlay[_ <: EditorCustomElementRenderer]): GutterIconRenderer =
      new ErrorGutterRenderer(AllIcons.General.Error, label.getText)
  }

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
    inlayModel
      .getAfterLineEndElementsInRange(0, document.getLineEndOffset(document.getLineCount - 1), classOf[ErrorLabel])
      .asScala
      .foreach(_.dispose())

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

      inlayModel.addAfterLineEndElement(error.getTextOffset, true, errorLabel)
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
