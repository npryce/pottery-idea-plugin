package com.natpryce.pottery

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.nio.file.Paths
import java.time.Instant
import java.util.Random

class ProjectHistoryTest {
    val fakeRandom = object : Random() {
        var counter = 'a'
        override fun nextBytes(bytes: ByteArray) {
            bytes[bytes.size - 1] = counter.toByte()
            counter++
        }
    }
    
    val history = ProjectHistory(InMemoryProjectHistoryStorage(), fakeRandom)
    
    @Test
    fun `record and read back history`() {
        history.post(Instant.parse("2018-08-14T17:42:00Z"), POST_TYPE, "some content")
        
        val sherds = history.sherds(Span(Instant.parse("2018-08-14T00:00:00Z"), Instant.parse("2018-09-14T00:00:00Z")))
        
        assertThat(sherds, equalTo(listOf(
            Sherd(type= POST_TYPE, timestamp = Instant.parse("2018-08-14T17:42:00Z"), uid="AAAAAAAAAAAAAABh",
                file= Paths.get("docs/project-history/2018/2018-08/2018-08-14T17:42:00Z_post_AAAAAAAAAAAAAABh.md"))
        )))
    }
}
