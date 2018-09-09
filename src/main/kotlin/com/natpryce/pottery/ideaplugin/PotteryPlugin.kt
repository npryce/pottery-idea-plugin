package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory.SERVICE
import com.natpryce.pottery.JavaFileProjectHistoryStorage
import com.natpryce.pottery.ProjectHistory
import java.nio.file.Paths
import java.time.Clock


class PotteryPlugin : ToolWindowFactory, DumbAware {
    val clock = Clock.systemDefaultZone()
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val history = ProjectHistory(projectDir = { Paths.get(project.basePath) }, storage = JavaFileProjectHistoryStorage())
        
        val potteryPanel = PotteryPanel(project, history, clock)
        toolWindow.contentManager.addContent(SERVICE.getInstance().createContent(potteryPanel, "", false))
    
        (toolWindow as? ToolWindowEx)?.setTitleActions(DefaultActionGroup(
            PostSherdAction(project, history, clock)
        ))
    }
}
