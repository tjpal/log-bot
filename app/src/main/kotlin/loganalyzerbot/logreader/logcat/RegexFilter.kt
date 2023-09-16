package loganalyzerbot.logreader.logcat

import java.util.regex.Pattern

// Requires a regular expression which contains the keywords "contextId" and "subContextId"
// Both will be automatically replaced by the filter.
class RegexFilter(private val regex: String, contextIds: List<String>, subContextIDs: List<String>) :
    LogcatFilter(contextIds, subContextIDs) {

    private val pattern = Pattern.compile(createFinalRegexString())

    override fun filter(line: String): Triple<Int, String, String>? {
        val matcher = pattern.matcher(line)

        if(matcher.find()) {
            val contextId = matcher.group(1)
            val subContextId = matcher.group(2)

            return Triple(matcher.end(), contextId, subContextId)
        }

        return null
    }

    private fun createFinalRegexString(): String {
        val contextIdRegex = "(${contextIDs.joinToString("|")})"
        val subContextIdRegex = "(${subContextIDs.joinToString("|")})"

        return regex.
            replace("contextId", contextIdRegex).
            replace("subContextId", subContextIdRegex)
    }
}