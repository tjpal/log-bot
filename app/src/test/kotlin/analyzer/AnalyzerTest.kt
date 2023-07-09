package analyzer

import SequenceStateMachineHelper
import loganalyzerbot.analyzer.definition.SequenceDefinition
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnalyzerTest {
    @Test
    fun simpleSequenceIsMatched() {
        val sequenceDef = SequenceDefinition("TestSequence", "Start", "End")
        val matches = SequenceStateMachineHelper.runDefinitionOn(sequenceDef, "a", "Start", "x", "End", "y").matchedSequences

        assertTrue(matches.find { it.matchedSequence.name == "TestSequence" && it.finished} != null)
    }

    @Test
    fun sequenceWithoutEndIsMarkedAsUnfinished() {
        val sequenceDef = SequenceDefinition("TestSequence", "Start", "End")

        val matches = SequenceStateMachineHelper.runDefinitionOn(sequenceDef, "a", "Start", "x", "y").matchedSequences

        val sequence = matches.findLast { it.matchedSequence.name == "TestSequence" }
        assertNotNull(sequence)
        assertFalse(sequence.finished)
    }

    @Test
    fun subSequencesAreMatched() {
        val sequenceDef = SequenceDefinition("A", "Start_A", "End_A")
        val subSequence1 = SequenceDefinition("B", "Start_B", "End_B")
        val subSequence2 = SequenceDefinition("C", "Start_C", "End_C")

        sequenceDef.subSequences.add(subSequence1)
        sequenceDef.subSequences.add(subSequence2)

        val matches = SequenceStateMachineHelper.runDefinitionOn(sequenceDef,
            "a", "Start_A", "x", "Start_B",
            "y", "End_B", "z", "Start_C", "w", "End_C",
            "End_A", "b").matchedSequences

        val sequenceMatch = matches.find { it.matchedSequence.name == "A" && it.finished }
        assertNotNull(sequenceMatch)

        val subSequence1Match = sequenceMatch.matchedSubSequences.find { it.matchedSequence.name == "B" && it.finished }
        assertNotNull(subSequence1Match)

        val subSequence2Match = sequenceMatch.matchedSubSequences.find { it.matchedSequence.name == "C" && it.finished }
        assertNotNull(subSequence2Match)
    }
}