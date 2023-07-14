package loganalyzerbot.logreader

import java.util.*

data class LogMessage(
    val message: String,
    val creationTimestamp: Long,
    val storageTimestamp: Long,
    val contextId: UInt,
    val subContextId: UInt) {
    var id: UInt = 0U
}