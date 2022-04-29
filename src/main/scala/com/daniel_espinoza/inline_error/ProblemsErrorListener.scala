package com.daniel_espinoza.inline_error

import com.daniel_espinoza.inline_error.settings.InlineErrorState
import com.intellij.analysis.problemsView.toolWindow.HighlightingProblem
import com.intellij.analysis.problemsView.{Problem, ProblemsListener}
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

import java.util.concurrent

class ProblemsErrorListener extends ProblemsListener {

  import ProblemsErrorListener._

  override def problemAppeared(problem: Problem): Unit = {
    if (!isEnabled || !problem.isInstanceOf[HighlightingProblem]) return
    logger.debug(s"ProblemAppeared $problem")

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems.put(file, highlightingProblem :: problems.getOrDefault(file, List[HighlightingProblem]()))
    triggerHighlightEvent(file, problem.getProvider.getProject)
  }

  override def problemDisappeared(problem: Problem): Unit = {
    if (!isEnabled || !problem.isInstanceOf[HighlightingProblem]) return
    logger.debug(s"ProblemDisappeared $problem")

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems.replace(file, problems.getOrDefault(file, List[HighlightingProblem]()).filter(!_.equals(highlightingProblem)))
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
    settings.collector == InlineError.PROBLEMS
  }

  def triggerHighlightEvent(file: VirtualFile, project: Project): Unit = {
    if (!file.isValid) return
    val problemList = problems.getOrDefault(file, List[HighlightingProblem]())

    ApplicationManager.getApplication.invokeLater(() => {
      val errors = problemList.map(e => {
        InlineError.Error(e.getText, e.getLine, HighlightSeverity.ERROR)
      })
      logger.debug(s"Problems sent to InlineError:\n${errors.mkString("\n")}")

      InlineError.highlightError(errors, project)
    })
  }
}

object ProblemsErrorListener {
  val logger: Logger = Logger.getInstance(classOf[ProblemsErrorListener])
  val problems: concurrent.ConcurrentMap[VirtualFile, List[HighlightingProblem]] = new concurrent.ConcurrentHashMap[VirtualFile, List[HighlightingProblem]]()
}