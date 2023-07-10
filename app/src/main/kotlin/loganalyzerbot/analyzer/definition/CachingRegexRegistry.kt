package loganalyzerbot.analyzer.definition

import loganalyzerbot.logreader.LogMessage

class CachingRegexRegistry : RegexRegistryBase() {
    private val newRegexRegistrations = mutableListOf<Int>()
    private val logIdToMatchId = mutableMapOf<UInt, MutableList<Int>>()

    override fun preprocessMessages(messages: List<LogMessage>) {
        super.preprocessMessages(messages)
        for (regexId in newRegexRegistrations) {
            println("Processing regex $regexId")
            val regexEntry = idToRegexMap[regexId] ?: continue
            for (message in messages) {
                if (regexEntry.pattern.matcher(message.message).matches()) {
                    val matchList = logIdToMatchId[message.id] ?: mutableListOf<Int>(regexId)
                    matchList.add(regexId)
                }
            }
        }

        newRegexRegistrations.clear()
    }

    override fun registerRegex(regex: String): Int {
        if(regexMap.containsKey(regex)) {
            return regexMap[regex]!!.id
        }

        val id = super.registerRegex(regex)
        newRegexRegistrations.add(id)
        return id
    }

    override fun matches(input: String, id: Int): Boolean {
        val regexEntry = idToRegexMap[id] ?: return false
        return regexEntry.pattern.matcher(input).matches()
    }
}