package loganalyzerbot.logreader

import java.io.File

interface LogReader {
    fun read(logFile: File): List<LogMessage>
}