package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.application.Result
import com.intellij.openapi.application.WriteAction


fun doWriteAction(block: () -> Unit) {
    object : WriteAction<Nothing?>() {
        override fun run(result: Result<Nothing?>) {
            block()
        }
    }.execute().throwException()
}

