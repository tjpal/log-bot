package analyzer

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.analyzer.statemachine.SequenceStateMachine
import loganalyzerbot.logreader.LogMessage
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnalyzerTest {
    @Test
    fun matchSequenceStartAndEnd() {
        val sequenceDef = SequenceDefinition("TestSequence", Regex("Start"), Regex("End"))
        val matches = runDefinitionOn(sequenceDef, "a", "Start", "x", "End", "y").matchedSequences

        assertTrue(sequenceIsSuccessful("TestSequence", matches))
    }

    @Test
    fun sequenceWithoutAnd() {
        val sequenceDef = SequenceDefinition("TestSequence", Regex("Start"), Regex("End"))

        val matches = runDefinitionOn(sequenceDef, "a", "Start", "x", "y").matchedSequences

        val sequence = matches.findLast { it.matchedSequence.name == "TestSequence" }
        assertNotNull(sequence)
        assertFalse(sequence.finished)
    }

    private fun sequenceIsSuccessful(name: String, report: List<SequenceResult>): Boolean {
        return report.find { it.matchedSequence.name == name && it.finished} != null
    }

    private fun runDefinitionOn(sequenceDef: SequenceDefinition, vararg messages: String): SequenceStateMachine {
        val stateMachine = SequenceStateMachine(sequenceDef)

        for(message in messages) {
            stateMachine.process(LogMessage(message, Date(0), 0U, 0U))
        }
        stateMachine.onFinished()

        return stateMachine
    }
}