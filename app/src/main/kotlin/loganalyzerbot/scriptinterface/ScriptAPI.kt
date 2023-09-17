package loganalyzerbot.scriptinterface

import loganalyzerbot.logreader.logcat.RegexFilter

fun registerFilter(regex: String, contextIds: List<String>, subContextIds: List<String>) {
    ScriptHost.instance.logcatFilter.add(RegexFilter(regex, contextIds, subContextIds))
}