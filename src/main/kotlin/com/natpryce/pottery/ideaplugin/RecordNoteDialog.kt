package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.CCFlags.grow
import com.intellij.ui.layout.verticalPanel
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.recordNote
import java.time.Clock
import javax.swing.JComponent
import javax.swing.JTextArea


class RecordNoteDialog(
    project: Project?,
    private val history: ProjectHistory,
    private val clock: Clock

) : DialogWrapper(project, true, PROJECT) {
    
    val noteText = JTextArea(4, 40)
    
    init {
        title = "Record Sherd"
        setOKButtonText("Record")
        init()
    }
    
    override fun createCenterPanel(): JComponent {
        return verticalPanel {
            row("What happened?") {
                JBScrollPane(noteText)(grow)
            }
        }
    }
    
    override fun doOKAction() {
        history.recordNote(clock.instant(), noteText.text)
        VirtualFileManager.getInstance().asyncRefresh(null)
        super.doOKAction()
    }
}
