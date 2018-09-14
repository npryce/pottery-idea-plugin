package com.natpryce.pottery.ideaplugin

import com.intellij.util.ui.EditableModel
import javax.swing.table.DefaultTableModel

class SingleTextColumnTableModel(columnName: String) : DefaultTableModel(arrayOf(columnName), 0), EditableModel {
    override fun addRow() {
        addRow(arrayOf(""))
    }
    
    override fun exchangeRows(oldIndex: Int, newIndex: Int) {
        moveRow(oldIndex, oldIndex, newIndex)
    }
    
    override fun canExchangeRows(oldIndex: Int, newIndex: Int): Boolean {
        return true
    }
    
    fun values() =
        (0 until rowCount)
            .map { getValueAt(it, 0).toString() }
            .filter { it.isNotBlank() }
    
    fun isEmpty() =
        values().isEmpty()
    
}