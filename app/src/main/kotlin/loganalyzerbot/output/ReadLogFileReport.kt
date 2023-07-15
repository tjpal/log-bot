package loganalyzerbot.output

import loganalyzerbot.logreader.LogMessage
import java.io.File
import java.util.*

class ReadLogFileReport {
    fun write(logMessages: List<LogMessage>, file: File) {
        val stringBuilder = StringBuilder()

        logMessages.forEach { logMessage ->
            stringBuilder.append("${Date(logMessage.storageTimestamp)}: ${logMessage.message}\n")
        }

        file.writeText(stringBuilder.toString())
    }
}