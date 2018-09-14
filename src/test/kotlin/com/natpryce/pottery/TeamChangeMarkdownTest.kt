package com.natpryce.pottery

import com.oneeyedmen.okeydoke.junit.ApprovalsRule
import com.oneeyedmen.okeydoke.junit.ApprovalsRule.fileSystemRule
import org.junit.Rule
import org.junit.Test

class TeamChangeMarkdownTest {
    @Rule @JvmField
    val approval: ApprovalsRule = fileSystemRule("test/kotlin")
    
    @Test
    fun `joiners and leavers`() {
        approval.assertApproved(teamChangeMarkdown(
            joiners = listOf("alice", "bob", "carol"),
            leavers = listOf("dave", "eve", "fred")
        ))
    }
    
    @Test
    fun `one joiner`() {
        approval.assertApproved(teamChangeMarkdown(
            joiners = listOf("alice"),
            leavers = listOf("dave", "eve", "fred")
        ))
    }
    
    @Test
    fun `no joiner`() {
        approval.assertApproved(teamChangeMarkdown(
            joiners = listOf(),
            leavers = listOf("dave", "eve", "fred")
        ))
    }
    
    @Test
    fun `one leaver`() {
        approval.assertApproved(teamChangeMarkdown(
            joiners = listOf("alice", "bob", "carol"),
            leavers = listOf("dave")
        ))
    }
    
    @Test
    fun `no leaver`() {
        approval.assertApproved(teamChangeMarkdown(
            joiners = listOf("alice", "bob", "carol"),
            leavers = listOf()
        ))
    }
    
    
}