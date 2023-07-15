package loganalyzerbot.cmdline

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

enum class SORTMODE {
    STORAGE,
    CREATION
}
class CommandLineArgs(parser: ArgParser) {
    val logDirectory by parser.argument(ArgType.String, description = "Directory containing the log files")
    val scriptDirectory by parser.argument(ArgType.String, description = "Directory containing the script files")
    val reportFilename by parser.argument(ArgType.String, description = "Filename of the report file")
    val developmentMode by parser.option(ArgType.Boolean, shortName = "d", description = "Development mode")
    private val sortingMode by parser.option(ArgType.Choice(listOf("storage", "creation"), { it }),
                                     shortName = "s", description = "Sorting mode").default("storage")

    val sortmode
        get() = when(sortingMode) {
        "storage" -> SORTMODE.STORAGE
        "creation" -> SORTMODE.CREATION
        else -> SORTMODE.STORAGE
    }
}