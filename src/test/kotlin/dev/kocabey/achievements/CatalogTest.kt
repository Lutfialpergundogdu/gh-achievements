package dev.kocabey.achievements

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatalogTest {

    private val allTexts: List<String> = Catalog.entries.flatMap { listOfNotNull(it.howToEarn.en, it.howToEarn.tr, it.tip?.en, it.tip?.tr) } +
        Catalog.faq.flatMap { listOf(it.question.en, it.question.tr, it.answer.en, it.answer.tr) }

    @Test
    fun `ids are unique`() {
        assertEquals(Catalog.entries.size, Catalog.entries.map { it.id }.toSet().size)
    }

    @Test
    fun `every tracked tier and one-time check has a catalog entry`() {
        TieredAchievement.entries.forEach { tier ->
            assertEquals(tier.thresholds, Catalog.byId(tier.id).thresholds)
            assertTrue(Catalog.byId(tier.id).trackable)
        }
        evaluate(sampleStats()).let { report -> (report.oneTime.keys + report.highlights.keys).forEach { Catalog.byId(it) } }
    }

    @Test
    fun `texts exist in both languages`() {
        allTexts.forEach { assertTrue(it.isNotBlank()) }
        Catalog.entries.forEach { assertTrue(it.howToEarn.en != it.howToEarn.tr, "${it.id} isn't translated") }
    }

    @Test
    fun `texts read like a person wrote them`() {
        allTexts.forEach { assertFalse('\u2014' in it, "Em dash in: $it") }
    }

    @Test
    fun `generated docs are up to date`() {
        Lang.entries.forEach { lang ->
            val file = File(if (lang == Lang.EN) "docs/ACHIEVEMENTS.md" else "docs/ACHIEVEMENTS.${lang.code}.md")
            assertEquals(
                renderCatalogMarkdown(lang).trimEnd(),
                file.readText().trimEnd(),
                "${file.path} is stale. Run: ./gradlew run --args=\"catalog --format markdown --lang ${lang.code} --output ${file.path}\"",
            )
        }
    }
}
