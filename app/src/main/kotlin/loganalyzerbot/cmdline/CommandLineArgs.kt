package loganalyzerbot.cmdline

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class CommandLineArgs(parser: ArgParser) {
    val logDirectory by parser.argument(ArgType.String, description = "Directory containing the log files")
    val scriptDirectory by parser.argument(ArgType.String, description = "Directory containing the script files")
    val reportFilename by parser.argument(ArgType.String, description = "Filename of the report file")
    val developmentMode by parser.option(ArgType.Boolean, shortName = "d", description = "Development mode")
}