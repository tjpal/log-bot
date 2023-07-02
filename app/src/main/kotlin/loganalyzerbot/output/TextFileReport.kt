package loganalyzerbot.output

import loganalyzerbot.analyzer.report.SequenceResult
import java.io.File

class TextFileReport {

    fun write(matchedSequences: List<SequenceResult>, file: File) {
        file.writeText(createReport(matchedSequences))
    }

    fun createReport(matchedSequences: List<SequenceResult>): String {
        val stringBuilder = StringBuilder()

        for(sequence in matchedSequences) {
            writeSequenceResult(sequence, stringBuilder, 0)
        }

        return stringBuilder.toString()
    }

    private fun writeSequenceResult(sequence: SequenceResult, stringBuilder: StringBuilder, level: Int) {
        tabPrefix(level, stringBuilder)
        stringBuilder.append("#Entry ${sequence.matchedSequence.name}: ${sequence.entryLogMessage.message}\n")

        for(subSequence in sequence.matchedSubSequences) {
            writeSequenceResult(subSequence, stringBuilder, level + 1)
        }

        if(sequence.exitLogMessage != null) {
            tabPrefix(level, stringBuilder)
            stringBuilder.append("#Exit ${sequence.matchedSequence.name}: ${sequence.exitLogMessage!!.message}\n")
        }
        else {
            tabPrefix(level, stringBuilder)
            stringBuilder.append("ERROR - Sequence {${sequence.matchedSequence.name} not finished\n")
        }
    }

    private fun tabPrefix(level: Int, stringBuilder: StringBuilder) {
        for(i in 0..level)
            stringBuilder.append("\t")
    }
}