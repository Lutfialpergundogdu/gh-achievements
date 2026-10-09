package dev.kocabey.achievements

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenderTest {

    private val stats = sampleStats()
    private val report = evaluate(stats)
    private val plain = Options(color = false)

    @Test
    fun `text report shows progress and tips`() {
        val text = renderText(stats, report, plain)
        assertContains(text, "GitHub achievements for @octocat")
        assertContains(text, "unlocked, x2 at 16 (12 to go)")
        assertContains(text, "→ Look for small fixes")
        assertFalse("\u001B[" in text, "no ANSI codes without color")
    }

    @Test
    fun `tips can be turned off`() {
        assertFalse("→" in renderText(stats, report, plain.copy(tips = false)))
    }

    @Test
    fun `turkish text report`() {
        val text = renderText(stats, report, plain.copy(lang = Lang.TR))
        assertContains(text, "@octocat için GitHub rozetleri")
        assertContains(text, "açıldı, x2 için 16 (12 kaldı)")
        assertContains(text, "Kademeli")
    }

    @Test
    fun `json has one object for one user and an array for several`() {
        val single = Json.parseToJsonElement(renderJson(listOf(stats to report))).jsonObject
        assertEquals("octocat", single.getValue("login").jsonPrimitive.content)
        assertEquals("true", single.getValue("oneTime").jsonObject.getValue("quickdraw").jsonPrimitive.content)

        val other = sampleStats(login = "hubot")
        val many = Json.parseToJsonElement(renderJson(listOf(stats to report, other to evaluate(other)))).jsonArray
        assertEquals(2, many.size)
    }

    @Test
    fun `svg is valid xml and escapes user input`() {
        val tricky = sampleStats(login = "a<b&c")
        val svg = renderSvg(tricky, evaluate(tricky), plain)
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(svg.byteInputStream())
        assertContains(svg, "a&lt;b&amp;c")
        assertContains(svg, "prefers-color-scheme:dark")
    }

    @Test
    fun `svg theme can be forced`() {
        val dark = renderSvg(stats, report, plain.copy(theme = Theme.DARK))
        assertFalse("prefers-color-scheme" in dark)
        assertContains(dark, "#0d1117")
    }

    @Test
    fun `markdown is a table`() {
        val md = renderMarkdown(stats, report, plain)
        assertTrue(md.lines().count { it.startsWith("| **") } >= 6)
        assertContains(md, "Developer Program Member")
    }

    @Test
    fun `catalog renders in every format`() {
        assertContains(renderCatalogText(plain), "Pull Shark")
        assertContains(renderCatalogMarkdown(Lang.TR), "## Sık sorulan sorular")
        assertEquals(Catalog.entries.size, Json.parseToJsonElement(renderCatalogJson(Lang.EN)).jsonArray.size)
    }
}
