package loganalyzerbot.analyzer.definition

import loganalyzerbot.analyzer.matcher.Matcher
import loganalyzerbot.analyzer.matcher.SequenceMatcher
import java.util.regex.Pattern

class SequenceDefinition(val name: String) : DefinitionNode() {
    val children = mutableListOf<DefinitionNode>()

    fun sequence(name: String, initBlock: SequenceDefinition.() -> Unit): SequenceDefinition {
        val sequence = SequenceDefinition(name)
        sequence.initBlock()

        children.add(sequence)
        return sequence
    }

    fun expect(regex: String, initBlock: ExpectDefinition.() -> Unit = {}): ExpectDefinition {
        val expect = ExpectDefinition(Pattern.compile(regex))
        expect.initBlock()

        children.add(expect)
        return expect
    }

    override fun createMatcher(): Matcher {
        return SequenceMatcher(children.map { it.createMatcher() })
    }
}