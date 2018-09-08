package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileTypes.PLAIN_TEXT
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileCopyEvent
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileMoveEvent
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
import org.jdesktop.swingx.JXMonthView
import org.jdesktop.swingx.VerticalLayout
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode.SINGLE_INTERVAL_SELECTION
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
import javax.swing.BoxLayout
import javax.swing.JPanel

class PotteryPanel(
    private val project: Project,
    private val history: ProjectHistory,
    private val clock: Clock
) : Box(BoxLayout.Y_AXIS), Disposable {
    
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
        addPropertyChangeListener("firstDisplayedDay") { ev ->
            highlightDaysWithSherds()
        }
    }
    
    private val sherdsPanel = JPanel(VerticalLayout())
    
    private val fileListener = HistoryRefresher(history, ::refresh)
    
    init {
        add(JBSplitter().apply {
            firstComponent = monthView
            secondComponent = JBScrollPane(sherdsPanel)
            setProportion(0.0f)
        })
        refresh()
        LocalFileSystem.getInstance().addVirtualFileListener(fileListener)
    }
    
    override fun dispose() {
        LocalFileSystem.getInstance().removeVirtualFileListener(fileListener)
    }
    
    private fun showSherds(selection: SortedSet<Date>) {
        showSherds(selection.map { it.toDayTimespan() })
    }
    
    private fun refresh() {
        highlightDaysWithSherds()
        showSherds(monthView.selection)
    }
    
    private fun showSherds(days: List<Span<Instant>>) {
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
}

class HistoryRefresher(
    private val history: ProjectHistory,
    private val refresh: ()->Unit
) : VirtualFileListener {
    override fun fileCreated(event: VirtualFileEvent) {
        refreshIf(history.containsFile(event.file))
    }

    override fun fileCopied(event: VirtualFileCopyEvent) {
        refreshIf(history.containsFile(event.file))
    }

    override fun fileMoved(event: VirtualFileMoveEvent) {
        refreshIf(history.containsFile(event.newParent) || history.containsFile(event.oldParent))
    }

    override fun fileDeleted(event: VirtualFileEvent) {
        refreshIf(history.containsFile(event.file))
    }

    fun refreshIf(refreshFlag: Boolean) {
        if (refreshFlag) {
            refresh()
        }
    }

    private fun ProjectHistory.containsFile(file: VirtualFile): Boolean {
        return containsFile(file.path)
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



