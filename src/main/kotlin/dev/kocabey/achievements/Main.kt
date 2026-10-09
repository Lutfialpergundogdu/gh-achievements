package dev.kocabey.achievements

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.system.exitProcess

private const val USAGE = """Usage: gh-achievements [username] [--json] [--no-color]

Shows your progress towards GitHub profile achievements.
Without a username it checks the account your token belongs to.

Token: GITHUB_TOKEN or GH_TOKEN, or the GitHub CLI (`gh auth login`)."""

fun main(args: Array<String>) {
    if ("-h" in args || "--help" in args) {
        println(USAGE)
        return
    }

    val asJson = "--json" in args
    val color = "--no-color" !in args && System.console() != null && System.getenv("NO_COLOR") == null
    val login = args.firstOrNull { !it.startsWith("--") }

    val token = GitHubClient.resolveToken() ?: fail("No GitHub token found. Set GITHUB_TOKEN or run `gh auth login`.")
    val client = GitHubClient(token)

    try {
        val stats = client.fetchStats(login ?: client.fetchViewerLogin())
        val report = evaluate(stats)
        println(if (asJson) renderJson(stats, report) else renderText(stats, report, color))
    } catch (e: GitHubException) {
        fail(e.message ?: "GitHub request failed")
    }
}

private fun fail(message: String): Nothing {
    System.err.println("gh-achievements: $message")
    exitProcess(1)
}

fun renderText(stats: UserStats, report: Report, color: Boolean): String {
    val green = if (color) "\u001B[32m" else ""
    val dim = if (color) "\u001B[2m" else ""
    val bold = if (color) "\u001B[1m" else ""
    val reset = if (color) "\u001B[0m" else ""
    fun mark(ok: Boolean) = if (ok) "$green✔$reset" else "$dim✘$reset"

    return buildString {
        appendLine("${bold}GitHub achievements for @${stats.login}$reset")
        appendLine()
        appendLine("${bold}Tiered$reset")
        report.tiers.forEach { tier ->
            val target = tier.nextThreshold ?: tier.achievement.thresholds.last()
            val filled = (tier.count.coerceAtMost(target) * 10 / target).coerceIn(0, 10)
            val bar = "█".repeat(filled) + "░".repeat(10 - filled)
            val status = when {
                tier.nextThreshold == null -> "${green}maxed out$reset"
                tier.level == 0 -> "unlocks at ${tier.nextThreshold} (${tier.remaining} to go)"
                else -> "$green${tier.label}$reset, x${tier.level + 1} at ${tier.nextThreshold} (${tier.remaining} to go)"
            }
            val repo = if (tier.achievement == TieredAchievement.STARSTRUCK && stats.topRepo != null) "  $dim${stats.topRepo}$reset" else ""
            appendLine("  ${tier.achievement.title.padEnd(14)} $bar  ${"${tier.count} ${tier.achievement.unit}".padEnd(26)} $status$repo")
        }
        appendLine()
        appendLine("${bold}One-time$reset")
        appendLine("  ${mark(report.quickdraw)} Quickdraw            closed an issue or PR within 5 minutes")
        appendLine("  ${mark(report.yolo)} YOLO                 merged a PR without a review")
        appendLine("  ${mark(report.publicSponsor)} Public Sponsor       sponsors someone publicly")
        appendLine("  $dim?$reset Pair Extraordinaire  ${dim}not available through the API, check your profile$reset")
        appendLine()
        appendLine("${bold}Profile highlights$reset")
        report.highlights.forEach { (name, ok) -> appendLine("  ${mark(ok)} $name") }
        appendLine()
        append("${dim}Quickdraw and YOLO only look at the latest 100 PRs/issues. Private activity only shows up for your own account.$reset")
    }
}

fun renderJson(stats: UserStats, report: Report): String = Json { prettyPrint = true }.encodeToString(
    JsonObject.serializer(),
    buildJsonObject {
        put("login", stats.login)
        putJsonArray("tiered") {
            report.tiers.forEach { tier ->
                addJsonObject {
                    put("name", tier.achievement.title)
                    put("count", tier.count)
                    put("level", tier.level)
                    put("nextThreshold", tier.nextThreshold)
                }
            }
        }
        putJsonObject("oneTime") {
            put("Quickdraw", report.quickdraw)
            put("YOLO", report.yolo)
            put("Public Sponsor", report.publicSponsor)
        }
        putJsonObject("highlights") {
            report.highlights.forEach { (name, ok) -> put(name, ok) }
        }
        putJsonArray("notTrackable") { add("Pair Extraordinaire") }
    },
)
