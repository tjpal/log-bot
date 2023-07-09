package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition

class ScriptHost {
    companion object {
        var instance = ScriptHost()
    }

    val rootSequences = mutableListOf<SequenceDefinition>()
    val allSequences = mutableMapOf<String, SequenceDefinition>()
    val sequenceStack = mutableListOf<SequenceDefinition>()

    fun reset() {
        rootSequences.clear()
        allSequences.clear()
        sequenceStack.clear()
    }
}