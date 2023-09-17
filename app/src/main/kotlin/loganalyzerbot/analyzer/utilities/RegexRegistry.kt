package loganalyzerbot.analyzer.utilities

import loganalyzerbot.logreader.LogMessage

/*
  Stores all regular expressions registered by scripts. Having them in one place allows later optimizations.
 */
interface RegexRegistry {
    companion object {
        var instance: RegexRegistry = NonCachingRegexRegistry()
    }
    fun preprocessMessages(messages: List<LogMessage>)
    fun registerRegex(regex: String): Int
    fun matches(input: String, id: Int): Boolean
}