package com.natpryce.pottery

import java.nio.file.Path

interface ProjectHistoryStorage {
    fun readText(path: Path): String?
    fun writeText(path: Path, content: String)
    fun list(path: Path): Set<Path>
    fun isDir(path: Path): Boolean
}