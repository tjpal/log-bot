/*
    Copyright (c) 2023 Thomas P.
    Use of this source code is governed by the MIT license that can be found in the project root directory.
*/
package loganalyzerbot

import loganalyzerbot.logreader.dlt.DltReader
import java.io.File

fun main(vararg args: String) {
    if(args.size != 1) {
        println("Usage: log-bot <dlt-file>")
        return
    }

    DltReader().read(File(args[0])).filter { it.isLog }. forEach {
        println(it.message)
    }
}