package loganalyzerbot.scriptinterface

import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object ScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        "loganalyzerbot.scriptinterface.*"
    )
    jvm {
        dependenciesFromClassContext(
            ScriptInterface::class,
            wholeClasspath = true
        )
    }
    providedProperties(mapOf("host" to KotlinType(ScriptHost::class)))
})

object EvaluationConfig : ScriptEvaluationConfiguration({
    providedProperties(
        mapOf(
            "host" to ScriptHost()
        )
    )
})

class ScriptRunner {
    fun run(file: File): List<ScriptDiagnostic> {
        val script = file.readText()

        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<ScriptInterface>()
        val result = BasicJvmScriptingHost().eval(script.toScriptSource(),
            compilationConfiguration,
            EvaluationConfig)

        return result.reports
    }
}