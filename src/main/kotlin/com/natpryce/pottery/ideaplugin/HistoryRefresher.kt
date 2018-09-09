package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileCopyEvent
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileMoveEvent
import com.natpryce.pottery.ProjectHistory
import java.nio.file.Paths

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
        return containsFile(Paths.get(file.path))
    }
}