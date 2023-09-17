package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.logreader.logcat.LogcatFilter

class ScriptHost {
    companion object {
        var instance = ScriptHost()
    }

    val sequences = mutableListOf<SequenceDefinition>()
    val logcatFilter = mutableListOf<LogcatFilter>()

    fun reset() {
        sequences.clear()
        logcatFilter.clear()
    }
}
