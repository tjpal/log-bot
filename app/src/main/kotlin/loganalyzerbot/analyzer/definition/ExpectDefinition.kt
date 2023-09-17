package loganalyzerbot.analyzer.definition

import loganalyzerbot.analyzer.matcher.ExpectMatcher
import loganalyzerbot.analyzer.matcher.Matcher
import java.util.regex.Pattern

class ExpectDefinition(private val pattern: Pattern): DefinitionNode() {
    override fun createMatcher(): Matcher {
        return ExpectMatcher(pattern)
    }
}