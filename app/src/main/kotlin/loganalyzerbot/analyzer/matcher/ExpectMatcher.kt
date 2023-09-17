package loganalyzerbot.analyzer.matcher

import loganalyzerbot.logreader.LogMessage
import java.util.regex.Pattern

class ExpectMatcher(private val pattern: Pattern): Matcher() {
    override fun process(logMessage: LogMessage) {
        if(pattern.matcher(logMessage.message).matches()) {
            println("ExpectMatcher: ${logMessage.message}")
            finished = true
        }
    }
}