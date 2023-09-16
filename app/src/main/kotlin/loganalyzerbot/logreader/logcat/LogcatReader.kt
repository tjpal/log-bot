package loganalyzerbot.logreader.logcat

import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.LogReader
import java.io.File

class LogcatReader(private val filters: List<LogcatFilter>) : LogReader {
    private val TIMESTAMP_START_INDEX = 0
    private val TIMESTAMP_END_INDEX = 18

    private val contextToID = toIndexLookupMap(filters.map { it.contextIDs }.flatten())
    private val subContextToID = toIndexLookupMap(filters.map { it.subContextIDs }.flatten())

    init {
        contextToID[""] = 0U
        subContextToID[""] = 0U
    }

    override fun read(logFile: File): List<LogMessage> {
        return logFile.readLines().mapNotNull { line ->
            var matchingOffset = 0
            var matchingContextId = ""
            var matchingSubContextId = ""

            for(filter in filters) {
                val matchingFilter = filter.filter(line)

                if(matchingFilter != null) {
                    matchingOffset = matchingFilter.first
                    matchingContextId = matchingFilter.second
                    matchingSubContextId = matchingFilter.third
                    break
                }
            }

            if(matchingOffset > 0) {
                val payload = line.subSequence(matchingOffset, line.length).toString()
                val timestamp = line.substring(TIMESTAMP_START_INDEX, TIMESTAMP_END_INDEX)

                return@mapNotNull LogMessage(
                    payload,
                    0,
                    0,
                    timestamp,
                    contextToID[matchingContextId] ?: 0U,
                    subContextToID[matchingSubContextId] ?: 0U,)
            }
            null
        }
    }

    private fun toIndexLookupMap(list: List<String>): MutableMap<String, UInt> {
        return list.
            distinct().
            withIndex().
            associate { it.value to it.index.toUInt() + 1U }.
            toMutableMap()
    }
}