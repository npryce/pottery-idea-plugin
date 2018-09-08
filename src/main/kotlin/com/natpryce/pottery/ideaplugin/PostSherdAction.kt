package com.natpryce.pottery.ideaplugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.natpryce.pottery.POST_TYPE
import com.natpryce.pottery.ProjectHistory
import java.time.Clock

class PostSherdAction(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock = Clock.systemDefaultZone()

) : AnAction("Post", "Post a new sherd", AllIcons.ToolbarDecorator.Add) {
    override fun actionPerformed(e: AnActionEvent) {
        val s: String? = Messages.showInputDialog(project, "What happened?", "Post New Sherd", AllIcons.ToolbarDecorator.Add)
        if (s != null) {
            history.post(clock.instant(), POST_TYPE, s)
            
            VirtualFileManager.getInstance().asyncRefresh(null)
        }
    }
}
