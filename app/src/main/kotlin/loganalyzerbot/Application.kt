package loganalyzerbot

import loganalyzerbot.analyzer.definition.CachingRegexRegistry
import loganalyzerbot.analyzer.definition.RegexRegistry
import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.analyzer.statemachine.SequenceStateMachine
import loganalyzerbot.cmdline.CommandLineArgs
import loganalyzerbot.cmdline.LOGTYPE
import loganalyzerbot.cmdline.SORTMODE
import loganalyzerbot.common.FileChangeWatcher
import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.dlt.DltFilter
import loganalyzerbot.logreader.dlt.DltReader
import loganalyzerbot.logreader.logcat.LogcatReader
import loganalyzerbot.output.ReadLogFileReport
import loganalyzerbot.output.TextFileReport
import loganalyzerbot.scriptinterface.ScriptHost
import loganalyzerbot.scriptinterface.ScriptRunner
import java.io.File
import java.nio.file.Paths
import kotlin.script.experimental.api.ScriptDiagnostic

class Application {
    fun run(cmdLineArgs: CommandLineArgs) {
        val logFiles = listLogFiles(File(cmdLineArgs.logSource), cmdLineArgs.logtype)
        val scriptDirectory = File(cmdLineArgs.scriptDirectory)
        val reportFilename = File(cmdLineArgs.reportFilename)

        if(cmdLineArgs.developmentMode == true) {
            runDevelopmentMode(logFiles, scriptDirectory, reportFilename, cmdLineArgs.sortmode, cmdLineArgs.logtype)
        } else {
            runNormalMode(logFiles, scriptDirectory, reportFilename, cmdLineArgs.sortmode, cmdLineArgs.logtype)
        }
    }

    private fun runDevelopmentMode(logFiles: List<File>, scriptDirectory: File,
                                   reportFilename: File, sortMode: SORTMODE,
                                   logType: LOGTYPE) {
        val logMessages = readLogMessages(logFiles, sortMode, logType)

        val readLogFileReport = ReadLogFileReport()
        readLogFileReport.write(logMessages, Paths.get(reportFilename.parent, "log.txt").toFile())

        RegexRegistry.instance = CachingRegexRegistry()

        val fileChangeWatcher = FileChangeWatcher(scriptDirectory.absolutePath, "kts") {
            println("Script file changed. Re-running scripts and re-analyzing log files...")
            processLogMessagesInDevMode(logMessages, scriptDirectory, reportFilename)
        }

        processLogMessagesInDevMode(logMessages, scriptDirectory, reportFilename)
        fileChangeWatcher.watch()

        println("Press any key")
        readln()
    }

    private fun processLogMessagesInDevMode(logMessages: List<LogMessage>, scriptDirectory: File, reportFilename: File) {
        ScriptHost.instance.reset()

        runScriptFiles(scriptDirectory)
        RegexRegistry.instance.preprocessMessages(logMessages)

        val result = analyzeLogMessages(logMessages)
        writeReport(result, reportFilename)
    }

    private fun runNormalMode(logFiles: List<File>, scriptDirectory: File, reportFilename: File,
                              sortMode: SORTMODE, logType: LOGTYPE) {
        if(!runScriptFiles(scriptDirectory)) {
            println("ERROR: Script execution failed. See errors above.")
            return
        }

        val logMessages = readLogMessages(logFiles, sortMode, logType)
        RegexRegistry.instance.preprocessMessages(logMessages)

        val results = analyzeLogMessages(logMessages)

        writeReport(results, reportFilename)
    }

    private fun runScriptFiles(scriptFileDirectory: File): Boolean {
        val scriptRunner = ScriptRunner()

        val executionDiagnostics = scriptFileDirectory.walkBottomUp().
        filter { it.isFile && it.extension == "kts" }.
        map {
            scriptRunner.run(it)
        }.
        flatten()

        val errors = executionDiagnostics.
        filter { it.severity == ScriptDiagnostic.Severity.ERROR }.
        toList()

        errors.forEach { println(it) }

        return errors.isEmpty()
    }

    private fun readLogMessages(logFiles: List<File>, sortMode: SORTMODE, logType: LOGTYPE): List<LogMessage> {
        val logMessages = mutableListOf<LogMessage>()

        val logReader = when(logType) {
            LOGTYPE.DLT -> DltReader(DltFilter.DEFAULT)
            LOGTYPE.LOGCAT -> LogcatReader(ScriptHost.instance.logcatFilter)
        }

        logFiles.forEach { logFile ->
            logMessages.addAll(logReader.read(logFile))
        }

        if(sortMode == SORTMODE.STORAGE) {
            logMessages.sortBy { it.storageTimestamp }
        } else if(sortMode == SORTMODE.CREATION){
            logMessages.sortBy { it.creationTimestamp }
        }

        var nextID: UInt = 0U
        logMessages.forEach { it.id = nextID++ }

        return logMessages
    }

    private fun analyzeLogMessages(logMessages: List<LogMessage>): List<SequenceResult>{
        val sequences = ScriptHost.instance.rootSequences
        val stateMachines = sequences.map { SequenceStateMachine(it) }

        logMessages.forEach { logMessage ->
            stateMachines.forEach { stateMachine ->
                stateMachine.process(logMessage)
            }
        }

        stateMachines.forEach { it.onFinished() }

        return stateMachines.map { it.matchedSequences }.flatten()
    }

    private fun writeReport(results: List<SequenceResult>, reportFilename: File) {
        val textFileReport = TextFileReport()
        val report = textFileReport.createReport(results)

        println(report)
        textFileReport.write(results, reportFilename)
    }

    private fun listLogFiles(logSource: File, logType: LOGTYPE): List<File> {
        if(logSource.isFile()) {
            return listOf(logSource)
        }

        if(!logSource.isDirectory()) {
            throw IllegalArgumentException("Log source must be a file or directory")
        }

        val logFileExtension = when(logType) {
            LOGTYPE.DLT -> "dlt"
            LOGTYPE.LOGCAT -> "txt"
        }

        return logSource.
            walkBottomUp().
            filter { it.isFile && it.extension == logFileExtension }.
            toList()
    }
}