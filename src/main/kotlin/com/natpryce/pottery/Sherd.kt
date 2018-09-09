package com.natpryce.pottery

import java.nio.file.Path
import java.time.Instant

const val POST_TYPE = "post"

data class Sherd(val type: String, val timestamp: Instant, val uid: String, val file: Path)
