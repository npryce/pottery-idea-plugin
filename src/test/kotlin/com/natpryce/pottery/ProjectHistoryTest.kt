package com.natpryce.pottery

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isEmpty
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.Random

class ProjectHistoryTest {
    val fakeRandom = object : Random() {
        var counter = 0
        override fun nextBytes(bytes: ByteArray) {
            bytes[bytes.size - 1] = counter.toByte()
            counter++
        }
    }
    
    val history = ProjectHistory(InMemoryProjectHistoryStorage(), fakeRandom)
    
    @Test
    fun `record and read back history`() {
        history.post(t("2018-08-14T09:00:00Z"), NOTE_TYPE, "some content")
        history.post(t("2018-08-14T10:00:00Z"), NOTE_TYPE, "more content")
        
        val timespan = Span(Instant.parse("2018-08-14T00:00:00Z"), Instant.parse("2018-09-14T00:00:00Z"))
        
        assertTrue(history.hasSherdsWithin(timespan))
        assertThat(history.sherds(timespan), equalTo(
            listOf(
                Sherd(type = NOTE_TYPE, timestamp = t("2018-08-14T09:00:00Z"), uid = uid(0)),
                Sherd(type = NOTE_TYPE, timestamp = t("2018-08-14T10:00:00Z"), uid = uid(1)))
        ))
    }
    
    @Test
    fun `reading back period with no events`() {
        val timespan = Span(Instant.parse("2018-08-14T00:00:00Z"), Instant.parse("2018-09-14T00:00:00Z"))
        
        assertFalse(history.hasSherdsWithin(timespan))
        assertThat(history.sherds(timespan), isEmpty)
    }
    
    fun t(instantStr: String) = Instant.parse(instantStr)
    fun uid(n: Int) = "AAAAAAAAAAAAAAA${('A' + n)}"
}
