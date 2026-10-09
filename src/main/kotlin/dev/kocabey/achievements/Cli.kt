package dev.kocabey.achievements

enum class Format { TEXT, JSON, MARKDOWN, SVG }

enum class Theme { AUTO, LIGHT, DARK }

enum class Command { REPORT, CATALOG, HELP, VERSION }

data class Options(
    val command: Command = Command.REPORT,
    val users: List<String> = emptyList(),
    val format: Format = Format.TEXT,
    val lang: Lang = Lang.EN,
    val output: String? = null,
    val color: Boolean = true,
    val tips: Boolean = true,
    val theme: Theme = Theme.AUTO,
)

class UsageException(message: String) : Exception(message)

const val USAGE = """Usage:
  gh-achievements [username...] [options]   show achievement progress
  gh-achievements catalog [options]         list every achievement and how to earn it

Options:
  -f, --format <text|json|markdown|svg>   output format (default: text)
      --json                              same as --format json
  -l, --lang <en|tr>                      language (default: from your locale)
  -o, --output <file>                     write to a file instead of stdout
      --theme <auto|light|dark>           SVG theme (default: auto)
      --no-tips                           hide the "how to get the next tier" hints
      --no-color                          disable colors (NO_COLOR works too)
  -h, --help                              show this help
  -v, --version                           show the version

Without a username it checks the account your token belongs to.
Token: GITHUB_TOKEN or GH_TOKEN, or the GitHub CLI (`gh auth login`)."""

fun parseArgs(args: List<String>, defaultLang: Lang = Lang.EN): Options {
    var options = Options(lang = defaultLang)
    val users = mutableListOf<String>()
    val queue = ArrayDeque(args)

    fun value(flag: String): String = queue.removeFirstOrNull()?.takeUnless { it.startsWith("-") }
        ?: throw UsageException("$flag needs a value")

    while (queue.isNotEmpty()) {
        when (val arg = queue.removeFirst()) {
            "-h", "--help" -> options = options.copy(command = Command.HELP)
            "-v", "--version" -> options = options.copy(command = Command.VERSION)
            "--json" -> options = options.copy(format = Format.JSON)
            "-f", "--format" -> {
                val raw = value(arg)
                val format = Format.entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
                    ?: throw UsageException("Unknown format '$raw'. Use text, json, markdown or svg.")
                options = options.copy(format = format)
            }
            "-l", "--lang" -> {
                val raw = value(arg)
                options = options.copy(lang = Lang.fromCode(raw) ?: throw UsageException("Unknown language '$raw'. Use en or tr."))
            }
            "-o", "--output" -> options = options.copy(output = value(arg))
            "--theme" -> {
                val raw = value(arg)
                val theme = Theme.entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
                    ?: throw UsageException("Unknown theme '$raw'. Use auto, light or dark.")
                options = options.copy(theme = theme)
            }
            "--no-tips" -> options = options.copy(tips = false)
            "--no-color" -> options = options.copy(color = false)
            else -> when {
                arg.startsWith("-") -> throw UsageException("Unknown option '$arg'")
                arg == "catalog" && users.isEmpty() && options.command == Command.REPORT -> options = options.copy(command = Command.CATALOG)
                else -> users += arg.removePrefix("@")
            }
        }
    }

    if (options.command == Command.CATALOG && options.format == Format.SVG) throw UsageException("The catalog can't be rendered as SVG")
    if (options.format == Format.SVG && users.size > 1) throw UsageException("SVG cards are made for one user at a time")
    return options.copy(users = users)
}
