/*
    Copyright (c) 2023 Thomas P.
    Use of this source code is governed by the MIT license that can be found in the project root directory.
*/
package loganalyzerbot

import loganalyzerbot.analyzer.report.SequenceResult
import loganalyzerbot.analyzer.statemachine.SequenceStateMachine
import loganalyzerbot.logreader.LogMessage
import loganalyzerbot.logreader.dlt.DltFilter
import loganalyzerbot.logreader.dlt.DltReader
import loganalyzerbot.output.TextFileReport
import loganalyzerbot.scriptinterface.ScriptHost
import loganalyzerbot.scriptinterface.ScriptRunner
import java.io.File
import kotlin.script.experimental.api.ScriptDiagnostic

fun main(vararg args: String) {
    if(args.size != 3) {
        println("Usage: log-bot <dlt-file-directory> <script-directory> <report-filename>")
        return
    }

    val dltDirectory = File(args[0])
    val scriptDirectory = File(args[1])
    val reportFilename = File(args[2])

    if(!runScriptFiles(scriptDirectory)) {
        println("ERROR: Script execution failed. See errors above.")
        return
    }

    val logMessages = parseDltFiles(dltDirectory)
    val results = analyzeLogMessages(logMessages)

    writeReport(results, reportFilename)
}
fun runScriptFiles(scriptFileDirectory: File): Boolean {
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

fun parseDltFiles(dltDirectory: File): List<LogMessage> {
    val logMessages = mutableListOf<LogMessage>()

    dltDirectory.walkBottomUp().
    filter { it.isFile && it.extension == "dlt" }.
    forEach {
        val dltReader = DltReader(DltFilter.DEFAULT)
        logMessages.addAll(dltReader.read(it))
    }

    return logMessages
}

fun analyzeLogMessages(logMessages: List<LogMessage>): List<SequenceResult>{
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

fun writeReport(results: List<SequenceResult>, reportFilename: File) {
    val textFileReport = TextFileReport()
    val report = textFileReport.createReport(results)

    println(report)
    textFileReport.write(results, reportFilename)
}