package loganalyzerbot.analyzer.matcher

import loganalyzerbot.logreader.LogMessage

class SequenceMatcher(private val children: List<Matcher>) : Matcher() {
    private var currentChildIndex = 0

    override fun process(logMessage: LogMessage) {
        if(currentChildIndex >= children.size || finished) {
            throw IllegalStateException("Sequence matcher has finished, but still processing log messages")
        }

        if(error) {
            throw IllegalStateException("Sequence matcher has finished with error, but still processing log messages")
        }

        val currentChild = children[currentChildIndex]
        currentChild.process(logMessage)

        if(currentChild.finished) {
            currentChildIndex++

            if(currentChildIndex >= children.size) {
                finished = true
                return
            }
        }

        error = currentChild.error
    }
}