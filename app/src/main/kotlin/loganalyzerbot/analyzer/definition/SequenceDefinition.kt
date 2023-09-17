package loganalyzerbot.analyzer.definition

import loganalyzerbot.analyzer.utilities.RegexRegistry

class SequenceDefinition {
    var name: String = ""
    var subSequences: MutableList<SequenceDefinition> = mutableListOf()

    private var entryRegexId: Int = 0
    private var exitRegexId: Int = 0
    var exitRegex: String = ""
        set(value) { exitRegexId = RegexRegistry.instance.registerRegex(value) }

    constructor(name: String, entryRegex: String) : this(name, entryRegex, "")
    constructor(name: String, entryRegex: String, exitRegex: String) : this(name, entryRegex, exitRegex, mutableListOf())

    constructor(name: String, entryRegex: String, exitRegex: String, subSequences: MutableList<SequenceDefinition>) {
        this.name = name
        this.entryRegexId = RegexRegistry.instance.registerRegex(entryRegex)
        this.exitRegexId = RegexRegistry.instance.registerRegex(exitRegex)
        this.subSequences = subSequences
    }

    fun entryRegexMatches(input: String): Boolean {
        return RegexRegistry.instance.matches(input, entryRegexId)
    }

    fun exitRegexMatches(input: String): Boolean {
        return RegexRegistry.instance.matches(input, exitRegexId)
    }
}