package loganalyzerbot.scriptinterface

import loganalyzerbot.analyzer.definition.SequenceDefinition

fun sequenceStart(name: String, regex: String) {
    println("Adding sequence $name")
    ScriptHost.instance.sequences[name] = SequenceDefinition(name, Regex(regex))
}

fun sequenceEnd(name: String, regex: String) {
    println("Adding sequence end $name")
    val sequence = ScriptHost.instance.sequences[name]

    if(sequence != null) {
        sequence.exitRegex = Regex(regex)
    } else {
        println("Sequence $name not found. Add a sequene start definition first.")
        return
    }
}