package com.daniel_espinoza.inline_error.listeners

import com.daniel_espinoza.inline_error.InlineError
import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.codeInsight.daemon.impl.{HighlightInfo, HighlightInfoFilter}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiFile

import java.util.concurrent
import scala.annotation.tailrec

class HighlightErrorFilter extends HighlightInfoFilter {

  import HighlightErrorFilter._

  ApplicationManager.getApplication.executeOnPooledThread(new Runnable {
    @tailrec
    override def run(): Unit = {
      if (isEnabled) {
        ApplicationManager.getApplication.invokeLater(() =>
          ProjectManager.getInstance().getOpenProjects.foreach(InlineError.makeHighlightersInline)
        )
      }

      Thread.sleep(3000)
      run()
    }
  })

  def accept(highlightInfo: HighlightInfo, file: PsiFile): Boolean = {
    if (!isEnabled || file == null || !file.isValid) return true

    ApplicationManager.getApplication.invokeLater(() => {
      InlineError.makeHighlightersInline(file.getProject)
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
    })
  }

  def isEnabled: Boolean = {
    val settings = InlineErrorState.getInstance.getState

    if (settings == null) false
    else settings.collector == InlineError.HIGHLIGHT
  }
}

object HighlightErrorFilter {
  val logger: Logger = Logger.getInstance(HighlightErrorFilter.getClass)
  val problems: concurrent.ConcurrentMap[PsiFile, List[HighlightInfo]] = new concurrent.ConcurrentHashMap[PsiFile, List[HighlightInfo]]()
}