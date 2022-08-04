package com.daniel_espinoza.inline_error.listeners

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.daniel_espinoza.inline_error.{ErrorLabel, InlineError}
import com.intellij.codeInsight.daemon.impl.{HighlightInfo, HighlightInfoFilter}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiFile
import com.intellij.ui.components.JBLabel

import java.util.concurrent

class HighlightErrorFilter extends HighlightInfoFilter {

  import HighlightErrorFilter._

  def accept(highlightInfo: HighlightInfo, file: PsiFile): Boolean = {
    if (!isEnabled || file == null || !file.isValid) return true

    if (file.getViewProvider.getDocument == null) return true

    if (highlightInfo.getDescription != null && InlineError.filterSeverity(highlightInfo.getSeverity))
      problems.put(file, highlightInfo :: problems.getOrDefault(file, List[HighlightInfo]()).filter(_.getHighlighter != null))

    ApplicationManager.getApplication.invokeLater(() => {
      triggerHighlight(file)
    })

    true
  }

  def triggerHighlight(file: PsiFile): Unit = {
    val problemList = problems.getOrDefault(file, List[HighlightInfo]())

    ApplicationManager.getApplication.invokeLater(() => {
      val document = file.getViewProvider.getDocument

      val errors = problemList.filter(info => info.getHighlighter != null && info.getDescription != null).map(e => {
        InlineError.Error(e.getDescription, document.getLineNumber(e.getEndOffset), e.getSeverity)
      })

      if (errors.nonEmpty)
        logger.debug(s"Problems sent to InlineError:\n${errors.mkString("\n")}")

      InlineError.highlightErrorSeq(errors, file.getProject)
      InlineError.makeHighlightersInline(file.getProject)
    })
  }

  def isEnabled: Boolean = {
    val settings = InlineErrorState.getInstance.getState
    settings.collector == InlineError.HIGHLIGHT
  }
}

object HighlightErrorFilter {
  val logger: Logger = Logger.getInstance(HighlightErrorFilter.getClass)
  val problems: concurrent.ConcurrentMap[PsiFile, List[HighlightInfo]] = new concurrent.ConcurrentHashMap[PsiFile, List[HighlightInfo]]()
}