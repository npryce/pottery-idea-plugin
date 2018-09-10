package com.natpryce.pottery

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths

abstract class ProjectHistoryStorageContract {
    abstract val storage: ProjectHistoryStorage
    
    @Test
    fun `can save and load files`() {
        storage.writeText(Paths.get("an/example/path"), "some text")
        storage.writeText(Paths.get("another/path"), "some different text")
    
        assertThat(storage.readText(Paths.get("an/example/path")), equalTo("some text"))
        assertThat(storage.readText(Paths.get("another/path")), equalTo("some different text"))
    }
    
    @Test
    fun `returns null when loading nonexistent file`() {
        assertThat(storage.readText(Paths.get("whatever")), absent())
    }
    
    @Test
    fun `can list files in a directory`() {
        storage.writeText(Paths.get("a/b/file1"), "")
        storage.writeText(Paths.get("a/b/file2"), "")
        storage.writeText(Paths.get("a/c/file3"), "")
        storage.writeText(Paths.get("a/file4"), "")
    
        assertThat(storage.list(Paths.get("a")),
            equalTo(setOf(Paths.get("a/b"), Paths.get("a/c"), Paths.get("a/file4"))))
    
        assertThat(storage.list(Paths.get("a/b")),
            equalTo(setOf(Paths.get("a/b/file1"), Paths.get("a/b/file2"))))
    
        assertThat(storage.list(Paths.get("a/c")),
            equalTo(setOf(Paths.get("a/c/file3"))))
    }
    
    @Test
    fun `a nonexistent directory contains no files`() {
        assertThat(storage.list(Paths.get("whatever")), equalTo(emptySet()))
    }
    
    @Test
    fun `reports whether a path is a directory`() {
        storage.writeText(Paths.get("a/b/file1"), "")
        storage.writeText(Paths.get("a/file4"), "")
    
        Assert.assertFalse(storage.isDir(Paths.get("a/b/file1")))
        Assert.assertFalse(storage.isDir(Paths.get("a/file4")))
    
        Assert.assertTrue(storage.isDir(Paths.get("a/b/")))
        Assert.assertTrue(storage.isDir(Paths.get("a/b")))
        Assert.assertTrue(storage.isDir(Paths.get("a/")))
        Assert.assertTrue(storage.isDir(Paths.get("a")))
    }
    
    @Test
    fun `a nonexistent file is not a directory`() {
        Assert.assertFalse(storage.isDir(Paths.get("whatever")))
    }
}