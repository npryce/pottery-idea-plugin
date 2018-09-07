package com.natpryce.pottery

import java.io.File
import java.nio.charset.Charset
import java.security.SecureRandom
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.Random


private fun Random.sherdId() =
    ByteArray(12)
        .also { nextBytes(it) }
        .let { Base64.getUrlEncoder().encodeToString(it) }

private fun timeFormat(pattern: String): DateTimeFormatter =
    DateTimeFormatter.ofPattern(pattern, Locale.ROOT).withZone(ZoneOffset.UTC)

val yearDirectoryFormat = timeFormat("yyyy")
val yearMonthDirectoryFormat = timeFormat("yyyy-MM")

fun sherdPath(time: Instant, type: String, uid: String) =
    "${yearDirectoryFormat.format(time)}/${yearMonthDirectoryFormat.format(time)}${time}_${type}_${uid}.md"

class ProjectHistory(
    private val projectDir: () -> File,
    private val random: Random = SecureRandom.getInstanceStrong()
) {
    fun post(time: Instant, type: String, content: String) {
        File(projectHistoryDir(), sherdPath(time, type, random.sherdId()))
            .writeText(content, Charset.defaultCharset())
    }
    
    fun hasSherdsWithin(timespan: Span<Instant>): Boolean =
        sherdFilesWithin(timespan).any()
    
    fun sherds(timespan: Span<Instant>): List<Sherd> {
        return sherdFilesWithin(timespan)
            .map { sherdFromFile(it) }
            .toList()
    }
    
    private fun sherdFilesWithin(timespan: Span<Instant>): Sequence<File> {
        val sherdTimestampRange = timespan.map { it.toString() }
        
        return projectHistoryDir().listDirs().asSequence()
            .flatMap { yearDir -> yearDir.listDirs().asSequence() }
            .flatMap { monthDir -> monthDir.listFiles().asSequence() }
            .filter { it.name.substringBefore('_') in sherdTimestampRange }
    }
    
    private fun sherdFromFile(sherdFile: File): Sherd {
        val (timeStr, type, uid) = sherdFile.name.split('_', limit = 3)
        val time = Instant.parse(timeStr)
        
        return Sherd(type = type, timestamp = time, uid = uid, file = sherdFile)
    }
    
    private fun projectHistoryDir(): File {
        val projectDir = projectDir()
        return File(projectDir, ".project-history-dir")
            .takeIf { it.exists() }
            ?.let { File(projectDir, it.readPlatformText().trim()) }
            ?.takeIf { it.exists() }
            ?: File(projectDir, "docs/project-history")
    }
}
