package loganalyzerbot.analyzer.definition

import java.util.regex.Pattern

data class RegexEntry(val id: Int, val pattern: Pattern)

abstract class RegexRegistryBase : RegexRegistry {
    private var nextFreeRegexId = 0
    private val regexMap = mutableMapOf<String, RegexEntry>()
    protected val idToRegexMap = mutableMapOf<Int, RegexEntry>()

    override fun registerRegex(regex: String): Int {
        val id = nextFreeRegexId++
        val regexEntry = RegexEntry(id, Pattern.compile(regex))

        regexMap[regex] = regexEntry
        idToRegexMap[id] = regexEntry

        return id
    }
}