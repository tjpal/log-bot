package loganalyzerbot.logreader

import java.util.*

data class LogMessage(
    val message: String,
    val time: Date,
    val contextId: UInt,
    val subContextId: UInt)