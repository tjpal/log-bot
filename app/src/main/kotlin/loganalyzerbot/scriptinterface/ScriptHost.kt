package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.logreader.logcat.LogcatFilter

class ScriptHost {
    companion object {
        var instance = ScriptHost()
    }

    val rootSequences = mutableListOf<SequenceDefinition>()
    val allSequences = mutableMapOf<String, SequenceDefinition>()
    val sequenceStack = mutableListOf<SequenceDefinition>()
    val logcatFilter = mutableListOf<LogcatFilter>()

    fun reset() {
        rootSequences.clear()
        allSequences.clear()
        sequenceStack.clear()
    }
}