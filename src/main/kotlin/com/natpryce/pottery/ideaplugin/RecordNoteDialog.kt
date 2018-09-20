package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.recordNote
import java.awt.GridBagLayout
import java.time.Clock
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea


class RecordNoteDialog(
    project: Project?,
    private val history: ProjectHistory,
    private val clock: Clock

) : DialogWrapper(project, true, PROJECT) {
    
    val noteText = JTextArea(4, 40)
    
    init {
        title = "Record Note"
        setOKButtonText("Record")
        init()
    }
    
    override fun getPreferredFocusedComponent() = noteText
    
    override fun createCenterPanel(): JComponent {
        return JPanel(GridBagLayout()).apply {
            addToGrid(JBLabel("What happened?")) {
                at(0, 0)
                stretchX()
            }
            addToGrid(Box.createVerticalStrut(4)) {
                at(0, 1)
            }
            addToGrid(JBScrollPane(noteText)) {
                at(0, 2)
                stretch()
            }
        }
    }
    
    override fun doOKAction() {
        history.recordNote(clock.instant(), noteText.text)
        super.doOKAction()
    }
}
