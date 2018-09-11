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
        history.post(t("2018-08-14T09:00:00Z"), POST_TYPE, "some content")
        history.post(t("2018-08-14T10:00:00Z"), POST_TYPE, "more content")
        
        val sherds = history.sherds(Span(Instant.parse("2018-08-14T00:00:00Z"), Instant.parse("2018-09-14T00:00:00Z")))
        
        assertThat(sherds, equalTo(listOf(
            Sherd(type= POST_TYPE, timestamp = t("2018-08-14T09:00:00Z"), uid="AAAAAAAAAAAAAABh",
                file= Paths.get("docs/project-history/2018/2018-08/2018-08-14T09:00:00Z_post_AAAAAAAAAAAAAABh.md")),
            Sherd(type= POST_TYPE, timestamp = t("2018-08-14T10:00:00Z"), uid="AAAAAAAAAAAAAABi",
                file= Paths.get("docs/project-history/2018/2018-08/2018-08-14T10:00:00Z_post_AAAAAAAAAAAAAABi.md"))
        )))
    }
    
    fun t(instantStr: String) = Instant.parse(instantStr)
}
