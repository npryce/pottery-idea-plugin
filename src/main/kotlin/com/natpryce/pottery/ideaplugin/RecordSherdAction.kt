package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.natpryce.pottery.ProjectHistory
import java.time.Clock


class RecordSherdAction(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock
) : AnAction("Record", "Record an event", PotteryIcons.PostSherd) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = RecordSherdDialog(project, history, clock)
        dialog.show()
    }
}
