package loganalyzerbot.analyzer.statemachine

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.logreader.LogMessage

class SequenceStateMachine(private val sequenceDefinition: SequenceDefinition) {
    var matchedSequences = mutableListOf<SequenceResult>()
        private set
    private var sequenceStack = mutableListOf<Pair<SequenceDefinition, SequenceResult>>()

    fun process(message: LogMessage) {
        if(sequenceStack.isEmpty())
            matchSequenceEntry(message)
        else
            matchCurrentSequence(message)
    }

    fun onFinished() {
        if(sequenceStack.isNotEmpty()) {
            val topEntry = sequenceStack.last()
            topEntry.second.finished = false
            matchedSequences.add(topEntry.second)

            sequenceStack = mutableListOf()
        }
    }

    private fun matchSequenceEntry(message: LogMessage) {
        if(sequenceDefinition.entryRegex.matches(message.message)) {
            sequenceStack.add(Pair(sequenceDefinition, SequenceResult(sequenceDefinition, false)))
        }
    }

    private fun matchCurrentSequence(message: LogMessage) {
        val topEntry = sequenceStack.last()

        if(topEntry.first.exitRegex.matches(message.message)) {
            sequenceStack.removeLast()
            topEntry.second.finished = true
            matchedSequences.add(topEntry.second)
        }
    }
}