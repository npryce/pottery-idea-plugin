package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.natpryce.pottery.ProjectHistory
import java.time.Clock


class PotteryPlugin : ToolWindowFactory, DumbAware, Disposable {
    val clock = Clock.systemDefaultZone()
    var potteryPanel: PotteryPanel? = null
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val history = ProjectHistory(IdeaVfsProjectHistoryStorage(project))
        val potteryPanel = PotteryPanel(project, history, clock)
        val fileListener = ProjectHistoryViewRefresher(project, history, potteryPanel::refresh)
        
        toolWindow.contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent(potteryPanel, "", false))
        
        ApplicationManager.getApplication().getMessageBus().connect(this)
            .subscribe(VirtualFileManager.VFS_CHANGES, fileListener)
        
        this.potteryPanel = potteryPanel
    }
    
    override fun dispose() {
    }
}
