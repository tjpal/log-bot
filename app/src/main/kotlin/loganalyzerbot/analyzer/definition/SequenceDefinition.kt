package loganalyzerbot.analyzer.definition

class SequenceDefinition {
    constructor(name: String, entryRegex: Regex) : this(name, entryRegex, Regex(""))
    constructor(name: String, entryRegex: Regex, exitRegex: Regex) : this(name, entryRegex, exitRegex, mutableListOf())

    constructor(name: String, entryRegex: Regex, exitRegex: Regex, subSequences: MutableList<SequenceDefinition>) {
        this.name = name
        this.entryRegex = entryRegex
        this.exitRegex = exitRegex
        this.subSequences = subSequences
    }

    var name: String = ""
    var entryRegex: Regex = Regex("")
    var exitRegex: Regex = Regex("")
    var subSequences: MutableList<SequenceDefinition> = mutableListOf()
}