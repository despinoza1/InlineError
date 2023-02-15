package com.daniel_espinoza.inline_error.listeners

import com.daniel_espinoza.inline_error.InlineError
import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.codeInsight.daemon.impl.{HighlightInfo, HighlightInfoFilter}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiFile

class HighlightErrorFilter extends HighlightInfoFilter {

  def accept(highlightInfo: HighlightInfo, file: PsiFile): Boolean = {
    if (!isEnabled || file == null || !file.isValid) return true

    ApplicationManager.getApplication.invokeLater(() => {
      InlineError.makeHighlightersInline(file.getProject)
    })

    true
  }

  private def isEnabled: Boolean = {
    val settings = InlineErrorState.getInstance.getState

    if (settings == null) false
    else settings.collector == InlineError.HIGHLIGHT
  }
}