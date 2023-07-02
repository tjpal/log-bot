package loganalyzerbot.analyzer.report

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.logreader.LogMessage

class SequenceResult(val matchedSequence: SequenceDefinition,
                     var finished: Boolean,
                     var entryLogMessage: LogMessage) {
    val matchedSubSequences = mutableListOf<SequenceResult>()
    var exitLogMessage: LogMessage? = null
}