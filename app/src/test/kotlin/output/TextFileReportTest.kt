package output

import SequenceStateMachineHelper
import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.output.TextFileReport
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TextFileReportTest {
    @Test
    fun createReportWithSubsequences() {
        val sequenceDef = SequenceDefinition("A", "Start_A", "End_A")
        val subSequence1 = SequenceDefinition("B", "Start_B", "End_B")
        val subSequence2 = SequenceDefinition("C", "Start_C", "End_C")

        sequenceDef.subSequences.add(subSequence1)
        sequenceDef.subSequences.add(subSequence2)

        val matches = SequenceStateMachineHelper.runDefinitionOn(sequenceDef,
            "a", "Start_A", "x", "Start_B",
            "y", "End_B", "z", "Start_C", "w", "End_C",
            "End_A", "b").matchedSequences

        val report = TextFileReport()
        val result = report.createReport(matches)

        assertTrue(result.contains("#Entry A: Start_A"))

        assertTrue(result.contains("#Entry B: Start_B"))
        assertTrue(result.contains("#Exit B: End_B"))
        assertTrue(result.contains("#Entry C: Start_C"))
        assertTrue(result.contains("#Exit C: End_C"))

        assertTrue(result.contains("#Exit A: End_A"))
    }
}