package com.natpryce.pottery

import java.io.File

internal fun File.listDirs() = listFiles().filter(File::isDirectory)
