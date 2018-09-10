package com.natpryce.pottery

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class JavaIoProjectHistoryStorageTest: ProjectHistoryStorageContract() {
    @Rule @JvmField
    val tempDir = TemporaryFolder()
    
    override val storage by lazy { JavaIoProjectHistoryStorage(tempDir.root.toPath()) }
}
