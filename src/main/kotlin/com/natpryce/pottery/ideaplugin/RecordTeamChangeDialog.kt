package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.IdeModalityType.PROJECT
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
import javax.swing.event.TableModelEvent
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
        
        isOKActionEnabled = false
        val disableOkActionIfNoInput: (TableModelEvent) -> Unit = {
            isOKActionEnabled = !(joiners.isEmpty() && leavers.isEmpty())
        }
        joiners.addTableModelListener(disableOkActionIfNoInput)
        leavers.addTableModelListener(disableOkActionIfNoInput)
    }
    
    override fun createCenterPanel(): JComponent {
        return JBSplitter().apply {
            this.firstComponent = editableList("joined", joiners)
            this.secondComponent = editableList("left", leavers)
        }
    }
    
    private fun editableList(description: String, model: SingleTextColumnTableModel): JPanel {
        val table = object : JBTable(model) {
            override fun prepareEditor(editor: TableCellEditor?, row: Int, column: Int): Component {
                val field = super.prepareEditor(editor, row, column)
                (field as? JTextField)?.selectAll()
                field.requestFocus()
                return field
            }
        }
        table.cellEditor = DefaultCellEditor(JTextField())
        table.emptyText.text = "Nobody $description"
        return ToolbarDecorator.createDecorator(table)
            .setAddAction {
                model.addRow()
                table.editCellAt(model.rowCount - 1, 0)
            }
            .createPanel()
    }
    
    override fun doOKAction() {
        history.recordTeamChange(clock.instant(), joiners.values(), leavers.values())
        super.doOKAction()
    }
}
