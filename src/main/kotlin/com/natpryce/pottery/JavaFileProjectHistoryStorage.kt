package com.natpryce.pottery

import com.intellij.util.io.write
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.nio.file.Path

class JavaFileProjectHistoryStorage : ProjectHistoryStorage {
    override fun readText(path: Path) =
        try {
            path.toFile().readText(Charset.defaultCharset())
        }
        catch (e: FileNotFoundException) {
            null
        }
    
    override fun writeText(path: Path, content: String) {
        val file = path.toFile()
        file.parentFile.mkdirs()
        path.write(content)
        file.writeText(content, Charset.defaultCharset())
    }
    
    override fun list(path: Path) =
        path.toFile().listFiles().map { it.toPath() }
    
    override fun isDir(path: Path): Boolean {
        return path.toFile().isDirectory
    }
}