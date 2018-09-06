package com.natpryce.pottery.ideaplugin

import com.intellij.ui.EditorTextField
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class CancelPostAction(
    name: String,
    private val postTextArea: EditorTextField

) : AbstractAction(name) {
    
    override fun actionPerformed(e: ActionEvent?) {
        postTextArea.text = ""
    }
}
