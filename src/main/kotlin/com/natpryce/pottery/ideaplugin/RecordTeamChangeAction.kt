package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.natpryce.pottery.ProjectHistory
import java.time.Clock


class RecordTeamChangeAction(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock
) : AnAction("Team changed", "Record a team change", PotteryIcons.TeamChange) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = RecordSherdDialog(project, history, clock)
        dialog.show()
    }
}
