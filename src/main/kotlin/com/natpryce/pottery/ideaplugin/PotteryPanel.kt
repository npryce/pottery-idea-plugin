package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces.TOOLBAR
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileTypes.PLAIN_TEXT
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.CCFlags.grow
import com.intellij.ui.layout.CCFlags.push
import com.intellij.ui.layout.verticalPanel
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.Sherd
import com.natpryce.pottery.Span
import com.natpryce.pottery.days
import com.natpryce.pottery.map
import com.natpryce.pottery.timeOrder
import org.jdesktop.swingx.JXMonthView
import org.jdesktop.swingx.VerticalLayout
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.WEST
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle.LONG
import java.util.Date
import java.util.SortedSet
import javax.swing.Box
import javax.swing.Icon
import javax.swing.JPanel

class PotteryPanel(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock
) : JPanel(BorderLayout()) {
    
    private val actions =
        DefaultActionGroup(
            postSherdAction("Record an event", "Record an event", PotteryIcons.PostSherd, ::RecordSherdDialog),
            postSherdAction("Team changed", "Record a team change", PotteryIcons.TeamChange, ::RecordSherdDialog)
        )
    
    private val monthView = JXMonthView().apply {
        isTraversable = true
        selectionMode = SINGLE_INTERVAL_SELECTION
        selectionDate = Date.from(clock.instant())
        selectionModel.addDateSelectionListener { ev ->
            if (!ev.isAdjusting) {
                showSherds(ev.selection)
            }
        }
        addPropertyChangeListener("firstDisplayedDay") { _ ->
            highlightDaysWithSherds()
        }
    }
    
    private val sherdsPanel = JPanel(VerticalLayout())
    
    init {
        ActionManager.getInstance().createActionToolbar(TOOLBAR, actions, false)
            .apply {
                layoutPolicy = ActionToolbar.AUTO_LAYOUT_POLICY
                this.adjustTheSameSize(true)
            }
            .let { add(it.component, WEST) }
        
        add(JBSplitter().apply {
            firstComponent = monthView
            secondComponent = JBScrollPane(sherdsPanel)
            setProportion(0.0f)
        }, CENTER)
        
        refresh()
    }
    
    private fun showSherds(selection: SortedSet<Date>) {
        showSherds(selection.map { it.toDayTimespan() })
    }
    
    fun refresh() {
        highlightDaysWithSherds()
        showSherds(monthView.selection)
    }
    
    private fun showSherds(days: List<Span<Instant>>) {
        sherdsPanel.removeAll()
        days.flatMap { history.sherds(it) }
            .sortedWith(timeOrder.reversed())
            .forEachIndexed { i, sherd ->
                if (i > 0) sherdsPanel.add(Box.createVerticalStrut(8))
                sherdsPanel.add(sherdPane(sherd))
            }
        sherdsPanel.revalidate()
        sherdsPanel.repaint()
    }
    
    private fun sherdPane(sherd: Sherd) = verticalPanel {
        val sherdVirtualFile = project.baseDir.findFileByRelativePath(history.path(sherd).toString())
        
        if (sherdVirtualFile != null) {
            row(DateTimeFormatter.ofLocalizedDateTime(LONG).format(sherd.timestamp.atZone(ZoneId.systemDefault()))) {
                val document = FileDocumentManager.getInstance().getDocument(sherdVirtualFile)
                EditorTextField(document, project, PLAIN_TEXT, true, false)(grow, push)
            }
        }
    }
    
    private fun highlightDaysWithSherds() {
        val firstDate = monthView.firstDisplayedDay.toLocalDate()
        val endDate = monthView.lastDisplayedDay.toLocalDate().plusDays(1)
        
        Span(firstDate, endDate).days()
            .filter { day -> history.hasSherdsWithin(day.timespan()) }
            .map { day -> Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()) }
            .let { monthView.setFlaggedDates(*it.toTypedArray()) }
    }
    
    fun postSherdAction(text: String, description: String, icon: Icon, createDialog: (Project, ProjectHistory, Clock) -> DialogWrapper) =
        object : AnAction(text, description, icon) {
            override fun actionPerformed(e: AnActionEvent) {
                createDialog(project, history, clock).show()
            }
        }
}

private fun Date.toLocalDate(): LocalDate =
    OffsetDateTime.ofInstant(toInstant(), ZoneId.systemDefault()).toLocalDate()

private fun Date.toDayTimespan(): Span<Instant> {
    return toLocalDate().timespan()
}

private fun LocalDate.timespan(): Span<Instant> {
    return let { Span(it, it.plusDays(1)) }
        .map { it.atStartOfDay(ZoneId.systemDefault()).toInstant() }
}



