package loganalyzerbot.analyzer.matcher

import loganalyzerbot.logreader.LogMessage

abstract  class Matcher {
    var finished = false
    var error = false

    abstract fun process(logMessage: LogMessage)
}