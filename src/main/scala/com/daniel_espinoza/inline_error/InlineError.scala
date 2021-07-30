package com.daniel_espinoza.inline_error

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.{ActionManager, ActionPlaces, AnActionEvent, Presentation}
import com.intellij.psi.{PsiTreeChangeAdapter, PsiTreeChangeEvent}

class InlineError extends PsiTreeChangeAdapter {

  def triggerHighlightEvent(event: PsiTreeChangeEvent): Unit = {
    val presentation = new Presentation()
    presentation.putClientProperty("PsiFile", event.getFile)

    val actionManager = ActionManager.getInstance()
    actionManager
      .getAction("com.daniel_espinoza.inline_error.InlineErrorAction")
      .actionPerformed(new AnActionEvent(null,
        DataManager.getInstance().getDataContextFromFocusAsync.blockingGet(100),
        ActionPlaces.UNKNOWN,
        presentation,
        ActionManager.getInstance(),
        0))
  }

  override def childAdded(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childRemoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childReplaced(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childrenChanged(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childMoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)
}