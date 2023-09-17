package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.logreader.logcat.RegexFilter

fun registerFilter(regex: String, contextIds: List<String>, subContextIds: List<String>) {
    ScriptHost.instance.logcatFilter.add(RegexFilter(regex, contextIds, subContextIds))
}

fun sequence(name: String, initBlock: SequenceDefinition.() -> Unit) {
    val sequence = SequenceDefinition(name)
    sequence.initBlock()
    ScriptHost.instance.sequences.add(sequence)
}