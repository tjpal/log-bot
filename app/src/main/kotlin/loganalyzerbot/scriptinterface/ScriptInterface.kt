package loganalyzerbot.scriptinterface

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Kotlin script",
    fileExtension = "kts",
    compilationConfiguration = ScriptConfiguration::class,
    evaluationConfiguration = EvaluationConfig::class
)
interface ScriptInterface