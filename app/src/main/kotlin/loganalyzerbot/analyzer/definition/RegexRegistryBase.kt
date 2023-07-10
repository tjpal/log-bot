package loganalyzerbot.analyzer.definition

import java.util.regex.Pattern

data class RegexEntry(val id: Int, val pattern: Pattern)

open class RegexRegistryBase : RegexRegistry {
    private var nextFreeRegexId = 0
    private val regexMap = mutableMapOf<String, RegexEntry>()
    private val idToRegexMap = mutableMapOf<Int, RegexEntry>()

    companion object {
        val instance = RegexRegistryBase()
    }

    override fun registerRegex(regex: String): Int {
        val id = nextFreeRegexId++
        val regexEntry = RegexEntry(id, Pattern.compile(regex))

        regexMap[regex] = regexEntry
        idToRegexMap[id] = regexEntry

        return id
    }

    override fun matches(input: String, id: Int): Boolean {
        val regexEntry = idToRegexMap[id] ?: return false
        return regexEntry.pattern.matcher(input).matches()
    }
}