package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.CCFlags.grow
import com.intellij.ui.layout.verticalPanel
import com.natpryce.pottery.POST_TYPE
import com.natpryce.pottery.ProjectHistory
import java.time.Clock
import javax.swing.JComponent
import javax.swing.JTextArea


class RecordSherdDialog(
    project: Project,
    private val history: ProjectHistory,
    private val clock: Clock

) : DialogWrapper(project, true, PROJECT) {
    
    val sherdText = JTextArea(4, 40)
    
    init {
        title = "Record Sherd"
        setOKButtonText("Record")
        init()
    }
    
    override fun createCenterPanel(): JComponent {
        return verticalPanel {
            row("What happened?") {
                JBScrollPane(sherdText)(grow)
            }
        }
    }
    
    override fun doOKAction() {
        history.post(clock.instant(), POST_TYPE, sherdText.text)
        VirtualFileManager.getInstance().asyncRefresh(null)
        super.doOKAction()
    }
}
