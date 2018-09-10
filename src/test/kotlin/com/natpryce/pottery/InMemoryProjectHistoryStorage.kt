package com.natpryce.pottery

import java.nio.file.Path

class InMemoryProjectHistoryStorage : ProjectHistoryStorage {
    private val storedFiles = mutableMapOf<Path,String>()
    
    override fun readText(path: Path): String? {
        return storedFiles[path]
    }
    
    override fun writeText(path: Path, content: String) {
        storedFiles[path] = content
    }
    
    override fun list(path: Path): Set<Path> {
        return storedFiles.keys
            .filter { path.contains(it) }
            .map { path.resolve(it.getName(path.nameCount)) }
            .toSortedSet()
    }
    
    override fun isDir(path: Path): Boolean {
        return storedFiles.keys.any { path.contains(it) }
    }
    
    fun Path.contains(other: Path) =
        other.startsWith(this) && other != this
}