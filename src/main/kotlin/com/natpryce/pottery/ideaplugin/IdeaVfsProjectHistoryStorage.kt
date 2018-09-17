package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.refreshVfs
import com.natpryce.pottery.ProjectHistoryStorage
import java.nio.file.Path
import java.nio.file.Paths

class IdeaVfsProjectHistoryStorage(
    private val project: Project
) : ProjectHistoryStorage {
    
    override fun readText(path: Path) =
        projectDir()?.findFileByRelativePath(path.toString())
            ?.let { VfsUtil.loadText(it) }
    
    override fun writeText(path: Path, content: String) {
        val fullPath = Paths.get(project.basePath).resolve(path)
        
        WriteAction.run<Exception> {
            VfsUtil.createDirectories(fullPath.parent.toString())
                .createChildData(this, fullPath.fileName.toString())
                .let { VfsUtil.saveText(it, content) }
            
            fullPath.parent.refreshVfs()
        }
    }
    
    override fun list(path: Path): Set<Path> {
        val baseDir = projectDir()
        val basePath = Paths.get(project.basePath ?: ".")
        
        return baseDir?.findFileByRelativePath(path.toString())
            ?.children?.map { basePath.relativize(Paths.get(it.path)) }
            ?.toSortedSet()
            ?: emptySet()
    }
    
    override fun isDir(path: Path) =
        projectDir()?.findFileByRelativePath(path.toString())?.isDirectory ?: false
    
    private fun projectDir() =
        project.basePath?.let(LocalFileSystem.getInstance()::findFileByPath)
}
