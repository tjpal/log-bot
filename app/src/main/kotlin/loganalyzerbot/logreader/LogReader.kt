package loganalyzerbot.logreader

import java.io.File

interface LogReader {
    fun read(dltFile: File): Array<LogMessage>
}