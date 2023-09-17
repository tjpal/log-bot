package loganalyzerbot.analyzer.definition

import loganalyzerbot.analyzer.matcher.Matcher

abstract class DefinitionNode {
    abstract fun createMatcher(): Matcher
}
