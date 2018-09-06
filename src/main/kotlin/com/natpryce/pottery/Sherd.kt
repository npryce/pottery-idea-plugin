package com.natpryce.pottery

import java.io.File
import java.time.Instant

const val POST_TYPE = "post"

data class Sherd(val type: String, val timestamp: Instant, val uid: String, val file: File)
