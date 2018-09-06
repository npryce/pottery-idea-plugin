package com.natpryce.pottery.ideaplugin

import com.intellij.ui.EditorTextField
import com.natpryce.pottery.POST_TYPE
import com.natpryce.pottery.ProjectHistory
import java.awt.event.ActionEvent
import java.time.Clock
import javax.swing.AbstractAction

class PostSherdAction(
    name: String,
    private val history: ProjectHistory,
    private val postTextArea: EditorTextField,
    private val clock: Clock = Clock.systemUTC()

) : AbstractAction(name) {
    
    override fun actionPerformed(e: ActionEvent) {
        doWriteAction {
            history.post(clock.instant(), POST_TYPE, postTextArea.text)
            postTextArea.text = ""
        }
    }
}
