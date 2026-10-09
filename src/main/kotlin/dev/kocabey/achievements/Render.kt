package dev.kocabey.achievements

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

private val prettyJson = Json { prettyPrint = true }

private fun TierProgress.statusText(ui: Ui): String = when {
    nextThreshold == null -> ui.maxedOut
    level == 0 -> ui.unlocksAt(nextThreshold, remaining!!)
    else -> ui.nextTier(ui.tierLabel(this), level + 1, nextThreshold, remaining!!)
}

private fun TierProgress.barFill(cells: Int): Int {
    val target = nextThreshold ?: achievement.thresholds.last()
    return (count.toLong().coerceAtMost(target.toLong()) * cells / target).toInt().coerceIn(0, cells)
}

// ---------------------------------------------------------------- text

fun renderText(stats: UserStats, report: Report, options: Options): String {
    val ui = Ui(options.lang)
    val c = options.color
    val green = if (c) "\u001B[32m" else ""
    val dim = if (c) "\u001B[2m" else ""
    val bold = if (c) "\u001B[1m" else ""
    val reset = if (c) "\u001B[0m" else ""
    fun mark(ok: Boolean) = if (ok) "$green✔$reset" else "$dim✘$reset"
    fun tip(id: String) = Catalog.byId(id).tip?.get(options.lang)?.takeIf { options.tips }

    return buildString {
        appendLine("$bold${ui.header(stats.login)}$reset")
        appendLine()
        appendLine("$bold${ui.tiered}$reset")
        report.tiers.forEach { tier ->
            val fill = tier.barFill(10)
            val bar = "█".repeat(fill) + "░".repeat(10 - fill)
            val amount = "${tier.count} ${tier.achievement.unit[options.lang]}"
            val status = if (tier.level > 0) "$green${tier.statusText(ui)}$reset" else tier.statusText(ui)
            val repo = if (tier.achievement == TieredAchievement.STARSTRUCK && stats.topRepo != null) "  $dim${stats.topRepo}$reset" else ""
            appendLine("  ${tier.achievement.title.padEnd(14)} $bar  ${amount.padEnd(26)} $status$repo")
            if (tier.nextThreshold != null) tip(tier.achievement.id)?.let { appendLine("  $dim  → $it$reset") }
        }
        appendLine()
        appendLine("$bold${ui.oneTime}$reset")
        report.oneTime.forEach { (id, ok) ->
            appendLine("  ${mark(ok)} ${Catalog.byId(id).title.padEnd(20)} ${ui.oneTimeHint.getValue(id)}")
            if (!ok) tip(id)?.let { appendLine("  $dim  → $it$reset") }
        }
        appendLine("  $dim?$reset ${"Pair Extraordinaire".padEnd(20)} $dim${ui.notTrackable}$reset")
        appendLine()
        appendLine("$bold${ui.highlights}$reset")
        report.highlights.forEach { (id, ok) -> appendLine("  ${mark(ok)} ${Catalog.byId(id).title}") }
        appendLine()
        append("$dim${ui.footnote}$reset")
    }
}

// ---------------------------------------------------------------- json

fun reportJson(stats: UserStats, report: Report): JsonObject = buildJsonObject {
    put("login", stats.login)
    putJsonArray("tiered") {
        report.tiers.forEach { tier ->
            addJsonObject {
                put("id", tier.achievement.id)
                put("name", tier.achievement.title)
                put("count", tier.count)
                put("level", tier.level)
                put("nextThreshold", tier.nextThreshold)
                put("remaining", tier.remaining)
            }
        }
    }
    stats.topRepo?.let { put("topRepo", it) }
    putJsonObject("oneTime") { report.oneTime.forEach { (id, ok) -> put(id, ok) } }
    putJsonObject("highlights") { report.highlights.forEach { (id, ok) -> put(id, ok) } }
    putJsonArray("notTrackable") {
        Catalog.entries.filter { it.availability == Availability.EARNABLE && !it.trackable }.forEach { add(it.id) }
    }
}

fun renderJson(results: List<Pair<UserStats, Report>>): String {
    val element: JsonElement = if (results.size == 1) {
        reportJson(results[0].first, results[0].second)
    } else {
        JsonArray(results.map { (stats, report) -> reportJson(stats, report) })
    }
    return prettyJson.encodeToString(JsonElement.serializer(), element)
}

// ---------------------------------------------------------------- markdown

fun renderMarkdown(stats: UserStats, report: Report, options: Options): String {
    val ui = Ui(options.lang)
    fun mark(ok: Boolean) = if (ok) "✅" else "⬜"
    return buildString {
        appendLine("### ${ui.header(stats.login)}")
        appendLine()
        appendLine("| | | |")
        appendLine("| --- | --- | --- |")
        report.tiers.forEach { tier ->
            val fill = tier.barFill(10)
            val bar = "▰".repeat(fill) + "▱".repeat(10 - fill)
            appendLine("| **${tier.achievement.title}** | `$bar` ${tier.count} ${tier.achievement.unit[options.lang]} | ${tier.statusText(ui)} |")
        }
        report.oneTime.forEach { (id, ok) ->
            appendLine("| **${Catalog.byId(id).title}** | ${mark(ok)} | ${ui.oneTimeHint.getValue(id)} |")
        }
        report.highlights.filterValues { it }.keys.forEach { id ->
            appendLine("| **${Catalog.byId(id).title}** | ✅ | ${ui.highlights} |")
        }
    }.trimEnd()
}

// ---------------------------------------------------------------- svg

private fun String.xml() = replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

private const val LIGHT = ".bg{fill:#ffffff;stroke:#d0d7de}.t{fill:#1f2328}.m{fill:#59636e}.track{fill:#eaeef2}.bar{fill:#2da44e}.ok{fill:#1a7f37}.no{fill:#afb8c1}"
private const val DARK = ".bg{fill:#0d1117;stroke:#30363d}.t{fill:#e6edf3}.m{fill:#9198a1}.track{fill:#21262d}.bar{fill:#3fb950}.ok{fill:#3fb950}.no{fill:#484f58}"

fun renderSvg(stats: UserStats, report: Report, options: Options): String {
    val ui = Ui(options.lang)
    val width = 495
    val rowHeight = 26
    val badges = report.oneTime + report.highlights.filterValues { it }
    val badgeRows = (badges.size + 1) / 2
    val height = 70 + report.tiers.size * rowHeight + 34 + badgeRows * 24 + 2

    val style = when (options.theme) {
        Theme.LIGHT -> LIGHT
        Theme.DARK -> DARK
        Theme.AUTO -> "$LIGHT@media (prefers-color-scheme:dark){$DARK}"
    }

    return buildString {
        appendLine("""<svg xmlns="http://www.w3.org/2000/svg" width="$width" height="$height" viewBox="0 0 $width $height" role="img" aria-labelledby="title">""")
        appendLine("""  <title id="title">${ui.header(stats.login).xml()}</title>""")
        appendLine("""  <style>text{font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Helvetica,Arial,sans-serif}.h{font-size:16px;font-weight:600}.s{font-size:13px}.x{font-size:11px}$style</style>""")
        appendLine("""  <rect class="bg" x="0.5" y="0.5" rx="6" width="${width - 1}" height="${height - 1}"/>""")
        appendLine("""  <text class="t h" x="20" y="34">${ui.header(stats.login).xml()}</text>""")

        var y = 70
        report.tiers.forEach { tier ->
            val fill = tier.barFill(100)
            appendLine("""  <text class="t s" x="20" y="$y">${tier.achievement.title.xml()}</text>""")
            appendLine("""  <rect class="track" x="130" y="${y - 10}" rx="4" width="150" height="10"/>""")
            if (fill > 0) appendLine("""  <rect class="bar" x="130" y="${y - 10}" rx="4" width="${150 * fill / 100}" height="10"/>""")
            appendLine("""  <text class="m x" x="292" y="$y">${"${tier.count} · ${tier.statusText(ui)}".xml()}</text>""")
            y += rowHeight
        }

        y += 8
        appendLine("""  <text class="t s" x="20" y="$y" font-weight="600">${ui.oneTime.xml()} &amp; ${ui.highlights.xml()}</text>""")
        y += 24
        badges.entries.forEachIndexed { index, (id, ok) ->
            val x = if (index % 2 == 0) 20 else 260
            val rowY = y + (index / 2) * 24
            val cls = if (ok) "ok" else "no"
            appendLine("""  <circle class="$cls" cx="${x + 6}" cy="${rowY - 4}" r="5"/>""")
            appendLine("""  <text class="t s" x="${x + 18}" y="$rowY">${Catalog.byId(id).title.xml()}</text>""")
        }
        append("</svg>")
    }
}

// ---------------------------------------------------------------- catalog

fun renderCatalogText(options: Options): String {
    val ui = Ui(options.lang)
    val c = options.color
    val bold = if (c) "\u001B[1m" else ""
    val dim = if (c) "\u001B[2m" else ""
    val reset = if (c) "\u001B[0m" else ""
    return buildString {
        appendLine("$bold${ui.catalogTitle}$reset")
        Availability.entries.forEach { availability ->
            val group = Catalog.entries.filter { it.availability == availability }
            if (group.isEmpty()) return@forEach
            appendLine()
            appendLine("$bold${ui.availability(availability)}$reset")
            group.forEach { entry ->
                val tiers = if (entry.thresholds.isNotEmpty()) "  $dim[${entry.thresholds.joinToString(" / ")}]$reset" else ""
                appendLine("  ${entry.title}$tiers")
                appendLine("    ${entry.howToEarn[options.lang]}")
                if (options.tips) entry.tip?.let { appendLine("    $dim→ ${it[options.lang]}$reset") }
            }
        }
    }.trimEnd()
}

fun renderCatalogMarkdown(lang: Lang): String {
    val ui = Ui(lang)
    val otherLang = if (lang == Lang.EN) Lang.TR else Lang.EN
    val otherFile = if (otherLang == Lang.TR) "ACHIEVEMENTS.tr.md" else "ACHIEVEMENTS.md"
    val otherName = if (otherLang == Lang.TR) "Türkçe" else "English"
    return buildString {
        appendLine("<!-- Generated by `gh-achievements catalog --format markdown --lang ${lang.code}`. Edit Catalog.kt instead. -->")
        appendLine()
        appendLine("# ${ui.catalogTitle}")
        appendLine()
        appendLine("[$otherName]($otherFile)")
        Availability.entries.forEach { availability ->
            val group = Catalog.entries.filter { it.availability == availability }
            if (group.isEmpty()) return@forEach
            appendLine()
            appendLine("## ${ui.availability(availability)}")
            if (availability == Availability.EARNABLE) {
                Category.entries.forEach { category ->
                    val inCategory = group.filter { it.category == category }
                    if (inCategory.isEmpty()) return@forEach
                    appendLine()
                    appendLine("### ${ui.category(category)}")
                    inCategory.forEach { entry ->
                        appendLine()
                        appendLine("#### ${entry.title}")
                        appendLine()
                        appendLine("**${ui.howToEarn}:** ${entry.howToEarn[lang]}")
                        if (entry.thresholds.isNotEmpty()) {
                            appendLine()
                            appendLine("**${ui.tiers}:** " + entry.thresholds.mapIndexed { i, t -> if (i == 0) "$t" else "x${i + 1}: $t" }.joinToString(" · "))
                        }
                        entry.tip?.let {
                            appendLine()
                            appendLine("**${ui.tip}:** ${it[lang]}")
                        }
                        appendLine()
                        appendLine("**${ui.tracked}:** ${if (entry.trackable) ui.yes else ui.no}")
                    }
                }
            } else {
                appendLine()
                group.forEach { appendLine("- **${it.title}:** ${it.howToEarn[lang]}") }
            }
        }
        appendLine()
        appendLine("## ${ui.faq}")
        Catalog.faq.forEach { item ->
            appendLine()
            appendLine("**${item.question[lang]}**")
            appendLine()
            appendLine(item.answer[lang])
        }
    }
}

fun renderCatalogJson(lang: Lang): String = prettyJson.encodeToString(
    JsonElement.serializer(),
    buildJsonArray {
        Catalog.entries.forEach { entry ->
            addJsonObject {
                put("id", entry.id)
                put("title", entry.title)
                put("category", entry.category.name.lowercase())
                put("availability", entry.availability.name.lowercase())
                put("trackable", entry.trackable)
                if (entry.thresholds.isNotEmpty()) putJsonArray("thresholds") { entry.thresholds.forEach { add(it) } }
                put("howToEarn", entry.howToEarn[lang])
                entry.tip?.let { put("tip", it[lang]) }
            }
        }
    },
)
