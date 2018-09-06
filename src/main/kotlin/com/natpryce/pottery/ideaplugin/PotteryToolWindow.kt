package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.CCFlags.grow
import com.intellij.ui.layout.CCFlags.push
import com.intellij.ui.layout.verticalPanel
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.Sherd
import org.jdesktop.swingx.JXMonthView
import org.jdesktop.swingx.VerticalLayout
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.LONG
import java.util.Date
import java.util.SortedSet
import javax.swing.Box
import javax.swing.JPanel

class PotteryToolWindow(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock
) : JBSplitter() {
    
    private val monthView = JXMonthView().apply {
        alignmentX = 0f
        alignmentY = 0.5f
        isTraversable = true
        selectionMode = SINGLE_INTERVAL_SELECTION
        selectionModel.addDateSelectionListener { ev ->
            println(ev)
            if (!ev.isAdjusting) {
                showSherds(ev.selection)
            }
        }
        addPropertyChangeListener { ev -> println(ev) }
        addActionListener { ev -> println(ev) }
    }
    
    private val sherdsPanel = JPanel(VerticalLayout())
    
    init {
        firstComponent = monthView
        secondComponent = JBScrollPane(sherdsPanel)
        setProportion(0.0f)
        showSherds(monthView.selection)
    }
    
    private fun showSherds(selection: SortedSet<Date>) {
        showSherds(selection.map { it.toDayTimespan() })
    }
    
    private fun Date.toDayTimespan(): ClosedRange<Instant> {
        val day = OffsetDateTime.ofInstant(toInstant(), ZoneId.systemDefault()).toLocalDate()
        val startOfDay = day.atStartOfDay(ZoneId.systemDefault())
        val endOfDay = startOfDay.plusDays(1).minusNanos(1)
        
        return startOfDay.toInstant()..endOfDay.toInstant()
    }
    
    private fun showSherds(days: List<ClosedRange<Instant>>) {
        sherdsPanel.removeAll()
        days.flatMap { history.sherds(it) }
            .sortedBy { it.file }
            .forEachIndexed { i, sherd ->
                if (i > 0) sherdsPanel.add(Box.createVerticalStrut(8))
                sherdsPanel.add(sherdPane(sherd))
            }
        sherdsPanel.revalidate()
        sherdsPanel.repaint()
    }
    
    private fun sherdPane(sherd: Sherd) = verticalPanel {
        val sherdVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(sherd.file)
        
        if (sherdVirtualFile != null) {
            row(DateTimeFormatter.ofLocalizedDateTime(LONG).format(sherd.timestamp.atZone(ZoneId.systemDefault()))) {
                val document = FileDocumentManager.getInstance().getDocument(sherdVirtualFile)
                EditorTextField(
                    document,
                    project,
                    FileTypes.PLAIN_TEXT,
                    true,
                    false
                )(grow, push)
            }
        }
    }
}