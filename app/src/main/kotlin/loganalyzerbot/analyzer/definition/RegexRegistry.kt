package loganalyzerbot.analyzer.definition

/*
  Stores all regular expressions registered by scripts. Having them in one place allows later optimizations.
 */
interface RegexRegistry {

    companion object {
        val instance = RegexRegistryBase()
    }
    fun registerRegex(regex: String): Int
    fun matches(input: String, id: Int): Boolean
}