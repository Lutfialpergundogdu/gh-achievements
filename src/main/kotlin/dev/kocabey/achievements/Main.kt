package dev.kocabey.achievements

import java.io.File
import kotlin.system.exitProcess

const val VERSION = "0.2.0"

fun main(args: Array<String>) {
    val options = try {
        parseArgs(args.toList(), Lang.fromEnvironment()).let { parsed ->
            val interactive = System.console() != null && System.getenv("NO_COLOR") == null && parsed.output == null
            parsed.copy(color = parsed.color && interactive)
        }
    } catch (e: UsageException) {
        fail("${e.message}\n\n$USAGE")
    }

    when (options.command) {
        Command.HELP -> println(USAGE)
        Command.VERSION -> println("gh-achievements $VERSION")
        Command.CATALOG -> emit(options, renderCatalog(options))
        Command.REPORT -> emit(options, renderReport(options))
    }
}

private fun renderCatalog(options: Options): String = when (options.format) {
    Format.JSON -> renderCatalogJson(options.lang)
    Format.MARKDOWN -> renderCatalogMarkdown(options.lang)
    else -> renderCatalogText(options)
}

private fun renderReport(options: Options): String {
    val token = GitHubClient.resolveToken() ?: fail("No GitHub token found. Set GITHUB_TOKEN or run `gh auth login`.")
    val client = GitHubClient(token)

    val results = try {
        val users = options.users.ifEmpty { listOf(client.fetchViewerLogin()) }
        users.map { login -> client.fetchStats(login).let { it to evaluate(it) } }
    } catch (e: GitHubException) {
        fail(e.message ?: "GitHub request failed")
    }

    return when (options.format) {
        Format.JSON -> renderJson(results)
        Format.SVG -> results.single().let { (stats, report) -> renderSvg(stats, report, options) }
        Format.MARKDOWN -> results.joinToString("\n\n") { (stats, report) -> renderMarkdown(stats, report, options) }
        Format.TEXT -> results.joinToString("\n\n") { (stats, report) -> renderText(stats, report, options) }
    }
}

private fun emit(options: Options, content: String) {
    val output = options.output
    if (output == null) {
        println(content)
    } else {
        File(output).apply { parentFile?.mkdirs() }.writeText(content + "\n")
        System.err.println("Wrote $output")
    }
}

private fun fail(message: String): Nothing {
    System.err.println("gh-achievements: $message")
    exitProcess(1)
}
