package com.natpryce.pottery.ideaplugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.natpryce.pottery.ProjectHistoryStorageContract
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runners.model.Statement


class IdeaVfsProjectHistoryStorageTest : ProjectHistoryStorageContract() {
    val projectBuilder = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder(javaClass.name)
    val fixture = IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.fixture);
    
    override val storage by lazy { IdeaVfsProjectHistoryStorage(fixture.project) }
    
    @Rule
    @JvmField
    val runOnDispatchThread = TestRule { base, _ ->
        object : Statement() {
            override fun evaluate() {
                fixture.setUp()
                
                try {
                    ApplicationManager.getApplication().invokeAndWait {
                        base.evaluate()
                    }
                }
                finally {
                    fixture.tearDown()
                }
            }
        }
    }
}

