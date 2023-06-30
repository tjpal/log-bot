package loganalyzerbot.logreader.dlt

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertContains


class DltReaderTest {
    @Test
    fun readDltFile() {
        val file = File(this.javaClass.classLoader.getResource("dlt-parser-test.dlt").file)
        val messages = DltReader(DltFilter.DEFAULT).read(file)

        assertContains(messages.map { it.message }, "Test Message #1")
        assertContains(messages.map { it.message }, "Test Message #2")
    }
}