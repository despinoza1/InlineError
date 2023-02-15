package com.daniel_espinoza.inline_error.listeners

import com.daniel_espinoza.inline_error.InlineError
import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.analysis.problemsView.toolWindow.HighlightingProblem
import com.intellij.analysis.problemsView.{Problem, ProblemsListener}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ProblemsErrorListener extends ProblemsListener {

  import ProblemsErrorListener._

  override def problemAppeared(problem: Problem): Unit = {
    if (!isEnabled || !problem.isInstanceOf[HighlightingProblem]) return
    logger.debug(s"ProblemAppeared $problem")

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  override def problemDisappeared(problem: Problem): Unit = {
    if (!isEnabled || !problem.isInstanceOf[HighlightingProblem]) return
    logger.debug(s"ProblemDisappeared $problem")

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  override def problemUpdated(problem: Problem): Unit = {
    if (!isEnabled || !problem.isInstanceOf[HighlightingProblem]) return
    logger.debug(s"ProblemUpdated $problem")

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  def isEnabled: Boolean = {
    val settings = InlineErrorState.getInstance.getState

    if (settings == null) false
    else settings.collector == InlineError.PROBLEMS
  }

  def triggerHighlightEvent(file: VirtualFile, project: Project): Unit = {
    if (!file.isValid) return

    ApplicationManager.getApplication.invokeLater(() => {
      InlineError.makeHighlightersInline(project)
    })
  }
}

object ProblemsErrorListener {
  val logger: Logger = Logger.getInstance(classOf[ProblemsErrorListener])
}