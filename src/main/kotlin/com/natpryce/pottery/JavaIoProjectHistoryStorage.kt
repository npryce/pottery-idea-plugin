package com.natpryce.pottery

import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.nio.file.Path

class JavaIoProjectHistoryStorage(private val projectDir: Path) : ProjectHistoryStorage {
    override fun readText(path: Path) =
        try {
            fileAt(path).readText(Charset.defaultCharset())
        }
        catch (e: FileNotFoundException) {
            null
        }
    
    override fun writeText(path: Path, content: String) {
        val file = fileAt(path)
        file.parentFile.mkdirs()
        file.writeText(content, Charset.defaultCharset())
    }
    
    override fun list(path: Path): Set<Path> {
        return fileAt(path).listFiles()?.map { projectDir.relativize(it.toPath()) }?.toSortedSet() ?: emptySet()
    }
    
    override fun isDir(path: Path) =
        fileAt(path).isDirectory
    
    private fun fileAt(path: Path) =
        projectDir.resolve(path).toFile()
}