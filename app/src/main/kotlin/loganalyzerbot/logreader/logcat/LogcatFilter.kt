package loganalyzerbot.logreader.logcat

abstract class LogcatFilter(val contextIDs: List<String>, val subContextIDs: List<String>) {
    abstract fun filter(line: String) : Triple<Int, String, String>?
}