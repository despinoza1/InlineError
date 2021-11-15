package com.daniel_espinoza.inline_error

import com.intellij.analysis.problemsView.toolWindow.HighlightingProblem
import com.intellij.analysis.problemsView.{Problem, ProblemsListener}
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.{ActionManager, AnActionEvent, Presentation}
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile

import scala.collection.mutable

class InlineError extends ProblemsListener {
  import InlineError._

  def triggerHighlightEvent(file: VirtualFile): Unit = {
    val presentation = new Presentation()
    val problemList = problems.getOrElse(file, List[HighlightingProblem]())
    presentation.putClientProperty(InlineErrorKey.key, problemList)

    logger.debug(s"Problems sent to InlineErrorAction:${problemList.foldLeft("")((msg, problem) => {
      s"$msg\n$problem [${problem.getLine}]: ${problem.getText}"
    })}")

    val actionManager = ActionManager.getInstance()
    actionManager
      .getAction("com.daniel_espinoza.inline_error.InlineErrorAction")
      .actionPerformed(new AnActionEvent(null,
        DataManager.getInstance().getDataContextFromFocusAsync.blockingGet(100),
        "InlineError",
        presentation,
        ActionManager.getInstance(),
        0))
  }

  override def problemAppeared(problem: Problem): Unit = {
    logger.debug(s"ProblemAppeared $problem ${problem.getText}")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems(file) = highlightingProblem :: problems.getOrElse(file, List[HighlightingProblem]())
    triggerHighlightEvent(file)
  }

  override def problemDisappeared(problem: Problem): Unit = {
    logger.debug(s"ProblemDisappeared $problem ${problem.getText}")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    problems(file) = problems.getOrElse(file, List[HighlightingProblem]()).filter(!_.equals(highlightingProblem))
    triggerHighlightEvent(file)
  }

  override def problemUpdated(problem: Problem): Unit = {
    logger.debug(s"ProblemUpdated $problem ${problem.getText}")
    if (!problem.isInstanceOf[HighlightingProblem]) return

    val highlightingProblem = problem.asInstanceOf[HighlightingProblem]
    val file = highlightingProblem.getFile

    triggerHighlightEvent(file)
  }
}

object InlineError {
  val logger: Logger = Logger.getInstance(classOf[InlineError])
  val problems: mutable.Map[VirtualFile, List[HighlightingProblem]] = mutable.Map()
}

object InlineErrorKey {
  val key: Key[List[HighlightingProblem]] = Key.create("InlineError")
}