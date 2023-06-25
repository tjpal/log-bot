package loganalyzerbot.logreader

import java.util.*

enum class LogType {
    LOG,
    CONTROL
}

data class LogMessage(val message: String,
                      val time: Date,
                      val context: String,
                      val subContext: String,
                      val type: LogType)