package loganalyzerbot.logreader

data class LogMessage(
    val message: String,
    val creationTimestamp: Long,
    val storageTimestamp: Long,
    val createTime: String,
    val contextId: UInt,
    val subContextId: UInt) {
    var id: UInt = 0U
}