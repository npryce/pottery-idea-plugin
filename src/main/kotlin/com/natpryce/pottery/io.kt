package com.natpryce.pottery

import java.io.File
import java.nio.charset.Charset

internal fun File.readPlatformText() = readText(Charset.defaultCharset())
internal fun File.listDirs() = listFiles().filter(File::isDirectory)
