package com.natpryce.pottery

import java.nio.file.Path
import java.nio.file.Paths
import java.security.SecureRandom
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.Random


const val NOTE_TYPE = "note"
const val TEAM_CHANGE_TYPE = "team-change"

data class Sherd(val type: String, val timestamp: Instant, val uid: String)

val timeOrder = compareBy(Sherd::timestamp, Sherd::type, Sherd::uid)

class ProjectHistory(
    private val storage: ProjectHistoryStorage,
    private val random: Random = SecureRandom.getInstanceStrong()
) {
    fun post(time: Instant, type: String, content: String) {
        storage.writeText(projectHistoryDir().resolve(sherdPath(time, type, random.sherdId())), content)
    }
    
    fun hasSherdsWithin(timespan: Span<Instant>): Boolean =
        sherdFilesWithin(timespan).any()
    
    fun sherds(timespan: Span<Instant>) =
        sherdFilesWithin(timespan)
            .map { sherdFromFile(it) }
            .toList()
    
    fun containsFile(path: Path) =
        path.startsWith(projectHistoryDir())
    
    private fun sherdFilesWithin(timespan: Span<Instant>): Sequence<Path> {
        val sherdTimestampRange = timespan.map { it.toString() }
        
        return projectHistoryDir().listDirs()
            .flatMap { yearDir -> yearDir.listDirs() }
            .flatMap { monthDir -> monthDir.listFiles().asSequence() }
            .filter { it.fileName.toString().substringBefore('_') in sherdTimestampRange }
    }
    
    private fun Path.listDirs(): Sequence<Path> =
        storage.list(this).filter { storage.isDir(it) }.asSequence()
    
    private fun Path.listFiles(): Sequence<Path> =
        storage.list(this).filterNot { storage.isDir(it) }.asSequence()
    
    private fun sherdFromFile(sherdFile: Path): Sherd {
        val (timeStr, type, uid) = sherdFile.fileName.toString().substringBefore('.').split('_', limit = 3)
        val time = Instant.parse(timeStr)
        
        return Sherd(type = type, timestamp = time, uid = uid)
    }
    
    private fun projectHistoryDir(): Path {
        return storage.readText(Paths.get(".project-history-dir"))
            ?.let { Paths.get(it.trim()) }
            ?: Paths.get("docs", "project-history")
    }
    
    fun path(sherd: Sherd) =
        projectHistoryDir().resolve(sherdPath(sherd.timestamp, sherd.type, sherd.uid))
}

fun ProjectHistory.recordNote(time: Instant, content: String) {
    post(time, NOTE_TYPE, content)
}

fun ProjectHistory.recordTeamChange(time: Instant, joiners: List<String>, leavers: List<String>) {
    post(time, TEAM_CHANGE_TYPE, teamChangeMarkdown(joiners, leavers))
}

private fun Random.sherdId() =
    ByteArray(12)
        .also { nextBytes(it) }
        .let { Base64.getUrlEncoder().encodeToString(it) }

private fun timeFormat(pattern: String): DateTimeFormatter =
    DateTimeFormatter.ofPattern(pattern, Locale.ROOT).withZone(ZoneOffset.UTC)

private val yearDirectoryFormat = timeFormat("yyyy")
private val yearMonthDirectoryFormat = timeFormat("yyyy-MM")
private val dateTimeFormat = timeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

private fun sherdPath(time: Instant, type: String, uid: String) =
    "${yearDirectoryFormat.format(time)}/${yearMonthDirectoryFormat.format(time)}/${dateTimeFormat.format(time)}_${type}_${uid}.md"


