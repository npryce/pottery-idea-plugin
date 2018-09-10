package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.natpryce.pottery.ProjectHistory
import java.nio.file.Path
import java.nio.file.Paths

class HistoryRefresher(
    private val project: Project,
    private val history: ProjectHistory,
    private val refresh: ()->Unit
) : BulkFileListener {
    
    override fun after(events: List<VFileEvent>) {
        refreshIf(
            events.flatMap { it.affectedPaths() }.any { history.containsFile(it) })
    }
    
    private fun VFileEvent.affectedPaths(): List<Path> {
        val baseDir = Paths.get(project.basePath)
        return when (this) {
            is VFileCreateEvent -> listOf(path)
            is VFileDeleteEvent -> listOf(path)
            is VFileMoveEvent -> listOf(oldPath, path)
            is VFileCopyEvent -> listOf(newParent.findChild(newChildName)?.path)
            else -> emptyList()
        }.filterNotNull().map { baseDir.relativize(Paths.get(it)) }
    }
    
    private fun refreshIf(refreshFlag: Boolean) {
        if (refreshFlag) {
            refresh()
        }
    }
}
