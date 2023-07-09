/*
    Copyright (c) 2023 Thomas P.
    Use of this source code is governed by the MIT license that can be found in the project root directory.
*/
package loganalyzerbot

import kotlinx.cli.ArgParser
import loganalyzerbot.cmdline.CommandLineArgs

fun main(args: Array<String>) {
    val parser = ArgParser("log-bot")
    val cmdLineArgs = CommandLineArgs(parser)
    parser.parse(args)

    Application().run(cmdLineArgs)
}
