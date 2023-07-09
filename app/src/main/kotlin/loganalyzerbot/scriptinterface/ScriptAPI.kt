package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition

fun sequenceStart(name: String, regex: String) {
    val scriptHost = ScriptHost.instance
    val definition = SequenceDefinition(name, regex)

    scriptHost.allSequences[name] = definition

    if(ScriptHost.instance.sequenceStack.isNotEmpty()) {
        val parentSequence = scriptHost.sequenceStack.last()
        parentSequence.subSequences.add(definition)
    } else {
        scriptHost.rootSequences.add(definition)
    }

    scriptHost.sequenceStack.add(definition)
}

fun sequenceEnd(name: String, regex: String) {
    if(ScriptHost.instance.sequenceStack.isEmpty()) {
        println("No sequence started. Add a sequence start definition first.")
        return
    }

    val currentSequence = ScriptHost.instance.sequenceStack.last()
    if(currentSequence.name != name) {
        println("Sequence $name not found. Add a sequence start definition first.")
        return
    }

    currentSequence.exitRegex = regex
    ScriptHost.instance.sequenceStack.removeLast()
}