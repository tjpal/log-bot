package loganalyzerbot

import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.analyzer.statemachine.SequenceStateMachine
import loganalyzerbot.cmdline.CommandLineArgs
import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.dlt.DltFilter
import loganalyzerbot.logreader.dlt.DltReader
import loganalyzerbot.output.TextFileReport
import loganalyzerbot.scriptinterface.ScriptHost
import loganalyzerbot.scriptinterface.ScriptRunner
import java.io.File
import kotlin.script.experimental.api.ScriptDiagnostic

class Application {
    fun run(cmdLineArgs: CommandLineArgs) {

        val dltDirectory = File(cmdLineArgs.logDirectory)
        val scriptDirectory = File(cmdLineArgs.scriptDirectory)
        val reportFilename = File(cmdLineArgs.reportFilename)

        if(!runScriptFiles(scriptDirectory)) {
            println("ERROR: Script execution failed. See errors above.")
            return
        }

        val logMessages = parseDltFiles(dltDirectory)
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

    private fun parseDltFiles(dltDirectory: File): List<LogMessage> {
        val logMessages = mutableListOf<LogMessage>()

        dltDirectory.walkBottomUp().
        filter { it.isFile && it.extension == "dlt" }.
        forEach {
            val dltReader = DltReader(DltFilter.DEFAULT)
            logMessages.addAll(dltReader.read(it))
        }

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
}