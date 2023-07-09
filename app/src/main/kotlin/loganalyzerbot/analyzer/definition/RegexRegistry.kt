package loganalyzerbot.analyzer.definition

import java.util.regex.Pattern

data class RegexEntry(val id: Int, val pattern: Pattern)

/*
  Stores all regular expressions registered by scripts. Having them in one place allows later optimizations.
 */
class RegexRegistry {
    private var nextFreeRegexId = 0
    private val regexMap = mutableMapOf<String, RegexEntry>()
    private val idToRegexMap = mutableMapOf<Int, RegexEntry>()

    fun registerRegex(regex: String): Int {
        val id = nextFreeRegexId++
        val regexEntry = RegexEntry(id, Pattern.compile(regex))

        regexMap[regex] = regexEntry
        idToRegexMap[id] = regexEntry

        return id
    }

    fun matches(input: String, id: Int): Boolean {
        val regexEntry = idToRegexMap[id] ?: return false
        return regexEntry.pattern.matcher(input).matches()
    }
}