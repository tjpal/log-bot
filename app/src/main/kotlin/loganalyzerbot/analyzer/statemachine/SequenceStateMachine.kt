package loganalyzerbot.analyzer.statemachine

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.logreader.LogMessage

class SequenceStateMachine(private val rootSequenceDefinition: SequenceDefinition) {
    var matchedSequences = mutableListOf<SequenceResult>()
        private set
    private var sequenceStack = mutableListOf<Pair<SequenceDefinition, SequenceResult>>()

    fun process(message: LogMessage) {
        if(sequenceStack.isEmpty())
            matchSequenceEntry(rootSequenceDefinition, message)
        else
            matchMessageForCurrentSequence(message)
    }

    fun onFinished() {
        if(sequenceStack.isNotEmpty()) {
            val topEntry = sequenceStack.last()
            topEntry.second.finished = false
            matchedSequences.add(topEntry.second)

            sequenceStack = mutableListOf()
        }
    }

    private fun matchSequenceEntry(definition: SequenceDefinition, message: LogMessage): Boolean {
        if(!definition.entryRegex.matches(message.message))
            return false

        sequenceStack.add(Pair(definition, SequenceResult(definition, false, message)))
        return true
    }

    private fun matchMessageForCurrentSequence(message: LogMessage) {
        val topEntry = sequenceStack.last()

        // Check weather we found the exit of the current sequence
        if(matchExit(topEntry, message))
            return

        // Check whether we found the entry of a sub-sequence
        if(matchSubSequenceEntry(topEntry, message))
            return
    }

    private fun matchSubSequenceEntry(entry: Pair<SequenceDefinition, SequenceResult>, message: LogMessage): Boolean {
        val subSequence = entry.first.subSequences.find {
            it.entryRegex.matches(message.message)
        } ?: return false

        val subSequenceEntry = Pair(subSequence, SequenceResult(subSequence, false, message))
        entry.second.matchedSubSequences.add(subSequenceEntry.second)
        sequenceStack.add(subSequenceEntry)

        return true
    }

    private fun matchExit(entry: Pair<SequenceDefinition, SequenceResult>, message: LogMessage): Boolean {
        if (!entry.first.exitRegex.matches(message.message))
            return false

        sequenceStack.removeLast()
        entry.second.finished = true
        entry.second.exitLogMessage = message

        if (sequenceStack.isNotEmpty()) {
            val parentEntry = sequenceStack.last()
            parentEntry.second.matchedSubSequences.add(entry.second)
        } else {
            matchedSequences.add(entry.second)
        }

        return true
    }
}