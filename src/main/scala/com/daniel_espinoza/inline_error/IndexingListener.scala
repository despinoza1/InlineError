package com.daniel_espinoza.inline_error

import com.intellij.analysis.problemsView.toolWindow.ProblemsView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.indexing.UnindexedFilesUpdaterListener

class IndexingListener extends UnindexedFilesUpdaterListener {
  import IndexingListener._

  var startupIndexing: Boolean = false

  override def updateStarted(project: Project): Unit = ()

  override def updateFinished(project: Project): Unit = {
    if (startupIndexing || project == null || project.isDisposed || !project.isOpen) return
    logger.debug(s"Starting ${ProblemsView.ID}")

    val toolWindowManager = ToolWindowManager.getInstance(project)
    val problemView = toolWindowManager.getToolWindow(ProblemsView.ID)

    ApplicationManager.getApplication.invokeLater(() => {
      val currentWindow = toolWindowManager.getActiveToolWindowId

      if (currentWindow != null && currentWindow != ProblemsView.ID) {
        logger.debug(s"Current ToolWindow=$currentWindow, changing to ${ProblemsView.ID}")
        problemView.show()
        logger.debug(s"Changing back to $currentWindow")
        toolWindowManager.getToolWindow(currentWindow).show()
      } else if (currentWindow == null) {
        logger.debug(s"No ToolWindow open, changing to ${ProblemsView.ID}")
        problemView.show()
        logger.debug("Hiding window")
        problemView.hide()
      }
    })

    startupIndexing = true
  }
}

object IndexingListener {
  val logger: Logger = Logger.getInstance(classOf[IndexingListener])
}
