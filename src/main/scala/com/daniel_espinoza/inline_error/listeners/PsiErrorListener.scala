package com.daniel_espinoza.inline_error.listeners

import com.daniel_espinoza.inline_error.InlineError
import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.{PsiTreeChangeAdapter, PsiTreeChangeEvent}

class PsiErrorListener extends PsiTreeChangeAdapter {
  override def childAdded(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childRemoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childReplaced(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childrenChanged(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childMoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  def triggerHighlightEvent(event: PsiTreeChangeEvent): Unit = {
    if (!isEnabled) return

    val file = event.getFile
    if (file == null) return
    val document = file.getViewProvider.getDocument

    ApplicationManager.getApplication.invokeLater(() => {
      InlineError.makeHighlightersInline(file.getProject)
    })
  }

  private def isEnabled: Boolean = {
    val settings = InlineErrorState.getInstance.getState

    if (settings == null) false
    else settings.collector == InlineError.PSIERROR
  }
}

object PsiErrorListener {
  val logger: Logger = Logger.getInstance(PsiErrorListener.getClass)
}
