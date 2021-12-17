package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.analysis.problemsView.toolWindow.HighlightingProblem
import com.intellij.analysis.problemsView.{Problem, ProblemsListener}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

import java.util.concurrent

class ProblemsErrorListener extends ProblemsListener {

  import ProblemsErrorListener._

  def triggerHighlightEvent(file: VirtualFile, project: Project): Unit = {
    val settings = InlineErrorState.getInstance.getState
    if (settings.psiEnabled) return
    if (!file.isValid) return

    val problemList = problems.getOrDefault(file, List[HighlightingProblem]())

    ApplicationManager.getApplication.invokeLater(() => {
      val errors = problemList.map(e => {
        InlineError.Error(e.getText, e.getLine)
      })
      logger.debug(s"Problems sent to InlineError:\n${errors.mkString("\n")}")

      InlineError.highlightError(errors, project)
    })
  }

  override def problemAppeared(problem: Problem): Unit = {
    logger.debug(s"ProblemAppeared $problem")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems.put(file, highlightingProblem :: problems.getOrDefault(file, List[HighlightingProblem]()))
    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  override def problemDisappeared(problem: Problem): Unit = {
    logger.debug(s"ProblemDisappeared $problem")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems.replace(file, problems.getOrDefault(file, List[HighlightingProblem]()).filter(!_.equals(highlightingProblem)))
    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  override def problemUpdated(problem: Problem): Unit = {
    logger.debug(s"ProblemUpdated $problem")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    triggerHighlightEvent(file, problem.getProvider.getProject)
  }
}

object ProblemsErrorListener {
  val logger: Logger = Logger.getInstance(classOf[ProblemsErrorListener])
  val problems: concurrent.ConcurrentMap[VirtualFile, List[HighlightingProblem]] = new concurrent.ConcurrentHashMap[VirtualFile, List[HighlightingProblem]]()
}