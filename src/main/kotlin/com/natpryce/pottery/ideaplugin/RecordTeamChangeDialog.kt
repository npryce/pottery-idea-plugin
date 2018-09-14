package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.JBSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.recordTeamChange
import java.awt.Component
import java.time.Clock
import javax.swing.DefaultCellEditor
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor


class RecordTeamChangeDialog(
    project: Project?,
    private val history: ProjectHistory,
    private val clock: Clock

) : DialogWrapper(project, true, PROJECT) {
    
    private val joiners = SingleTextColumnTableModel("Joiners")
    private val leavers = SingleTextColumnTableModel("Leavers")
    
    init {
        title = "Record Team Change"
        setOKButtonText("Record")
        init()
    }
    
    override fun createCenterPanel(): JComponent {
        return JBSplitter().apply {
            this.firstComponent = editableList(joiners)
            this.secondComponent = editableList(leavers)
        }
    }
    
    private fun editableList(model: DefaultTableModel): JPanel {
        val table = object : JBTable(model) {
            override fun prepareEditor(editor: TableCellEditor?, row: Int, column: Int): Component {
                val field = super.prepareEditor(editor, row, column)
                (field as? JTextField)?.selectAll()
                field.requestFocus()
                return field
            }
        }
        table.cellEditor = DefaultCellEditor(JTextField())
        return ToolbarDecorator.createDecorator(table)
            .setAddAction {
                model.addRow(arrayOf("Who?"))
                table.editCellAt(model.rowCount - 1, 0)
            }
            .createPanel()
    }
    
    override fun doOKAction() {
        history.recordTeamChange(clock.instant(), joiners.values(), leavers.values())
        VirtualFileManager.getInstance().asyncRefresh(null)
        super.doOKAction()
    }
}
