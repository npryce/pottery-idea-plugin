package com.natpryce.pottery

class InMemoryProjectHistoryStorageTest: ProjectHistoryStorageContract() {
    override val storage = InMemoryProjectHistoryStorage()
}
