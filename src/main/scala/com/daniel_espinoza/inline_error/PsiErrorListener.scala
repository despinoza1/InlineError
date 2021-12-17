package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiErrorElement, PsiFile, PsiTreeChangeAdapter, PsiTreeChangeEvent}

import scala.jdk.CollectionConverters._

class PsiErrorListener extends PsiTreeChangeAdapter {

  import PsiErrorListener._

  def triggerHighlightEvent(event: PsiTreeChangeEvent): Unit = {
    val settings = InlineErrorState.getInstance.getState
    if (settings.collector == InlineError.PROBLEMS) return

    val file = event.getFile
    val document = file.getViewProvider.getDocument

    ApplicationManager.getApplication.invokeLater(() => {
      val errors = getErrors(file).map(e => {
        InlineError.Error(e.getErrorDescription, document.getLineNumber(e.getTextOffset))
      })
      logger.debug(s"Problems sent to InlineError:\n${errors.mkString("\n")}")

      InlineError.highlightError(errors, file.getProject)
    })
  }

  def getErrors(psiFile: PsiFile): Seq[PsiErrorElement] = {
    if (psiFile != null)
      return PsiTreeUtil
        .collectElementsOfType(psiFile, classOf[PsiErrorElement])
        .asScala
        .toSeq
    Nil
  }
  
  override def childAdded(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childRemoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childReplaced(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childrenChanged(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)

  override def childMoved(event: PsiTreeChangeEvent): Unit = triggerHighlightEvent(event)
}

object PsiErrorListener {
  val logger: Logger = Logger.getInstance(PsiErrorListener.getClass)
}
