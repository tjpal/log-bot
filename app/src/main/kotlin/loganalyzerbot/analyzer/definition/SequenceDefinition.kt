package loganalyzerbot.analyzer.definition

class SequenceDefinition {
    constructor(name: String, entryRegex: Regex, exitRegex: Regex) {
        this.name = name
        this.entryRegex = entryRegex
        this.exitRegex = exitRegex
    }

    constructor(name: String, entryRegex: Regex, exitRegex: Regex, subSequences: MutableList<SequenceDefinition>) {
        this.name
        this.entryRegex = entryRegex
        this.exitRegex = exitRegex
        this.subSequences = subSequences
    }

    var name: String = ""
    var entryRegex: Regex = Regex("")
    var exitRegex: Regex = Regex("")
    var subSequences: MutableList<SequenceDefinition> = mutableListOf()
}