import loganalyzerbot.analyzer.definition.SequenceDefinition
import loganalyzerbot.analyzer.statemachine.SequenceStateMachine
import loganalyzerbot.logreader.LogMessage
import java.util.*

open class SequenceStateMachineHelper {
    companion object {
        fun runDefinitionOn(sequenceDef: SequenceDefinition, vararg messages: String): SequenceStateMachine {
            val stateMachine = SequenceStateMachine(sequenceDef)

            for(message in messages) {
                stateMachine.process(LogMessage(message, Date(0), 0U, 0U))
            }
            stateMachine.onFinished()

            return stateMachine
        }
    }
}