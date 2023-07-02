package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition

class ScriptHost {
    companion object {
        var instance = ScriptHost()
    }

    val sequences = mutableMapOf<String, SequenceDefinition>()
}