package loganalyzerbot.analyzer.utilities

class NonCachingRegexRegistry : RegexRegistryBase() {
    override fun matches(input: String, id: Int): Boolean {
        val regexEntry = idToRegexMap[id] ?: return false
        return regexEntry.pattern.matcher(input).matches()
    }
}