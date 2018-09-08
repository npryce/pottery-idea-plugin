import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import com.natpryce.pottery.ProjectHistory
import com.natpryce.pottery.ideaplugin.PotteryPanel
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.time.Clock
import javax.swing.JFrame


fun main(args: Array<String>) {
    val factory = IdeaTestFixtureFactory.getFixtureFactory()
    val fixture = factory
        .createCodeInsightFixture(
            factory.createLightFixtureBuilder().fixture,
            TempDirTestFixtureImpl())
    
    fixture.setUp()
    
    val project = fixture.project
    
    val history = ProjectHistory(projectDir = { File(project.basePath) })
    val clock = Clock.systemDefaultZone()
    
    history.post(clock.instant(), "post", "First post!")
    
    val frame = JFrame("Demo")
        .apply {
            contentPane.add(PotteryPanel(project, history, clock))
            pack()
            
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    fixture.tearDown()
                }
            })
        }
    frame.isVisible = true
}
