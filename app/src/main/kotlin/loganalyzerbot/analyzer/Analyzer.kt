package loganalyzerbot.analyzer

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.logreader.LogMessage

class Analyzer {
    fun analyze(logs: List<LogMessage>, sequences: List<SequenceDefinition>) {
        val sequenceMatchers = sequences.map { it.createMatcher() }.toMutableList()

        for(log in logs) {
            sequenceMatchers.forEachIndexed { index, matcher ->
                matcher.process(log)

                if (matcher.finished) {
                    sequenceMatchers[index] = sequences[index].createMatcher()
                }
            }
        }
    }
}