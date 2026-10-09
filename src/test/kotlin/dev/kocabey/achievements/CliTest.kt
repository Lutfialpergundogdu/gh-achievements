package dev.kocabey.achievements

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CliTest {

    @Test
    fun `no arguments means a text report for the token owner`() {
        val options = parseArgs(emptyList())
        assertEquals(Command.REPORT, options.command)
        assertEquals(emptyList(), options.users)
        assertEquals(Format.TEXT, options.format)
    }

    @Test
    fun `users, format, language and output are parsed`() {
        val options = parseArgs(listOf("@octocat", "torvalds", "-f", "markdown", "--lang", "tr", "-o", "out.md", "--no-tips"))
        assertEquals(listOf("octocat", "torvalds"), options.users)
        assertEquals(Format.MARKDOWN, options.format)
        assertEquals(Lang.TR, options.lang)
        assertEquals("out.md", options.output)
        assertFalse(options.tips)
    }

    @Test
    fun `json flag is a shortcut`() {
        assertEquals(Format.JSON, parseArgs(listOf("--json")).format)
    }

    @Test
    fun `catalog is a subcommand`() {
        val options = parseArgs(listOf("catalog", "--format", "markdown"))
        assertEquals(Command.CATALOG, options.command)
        assertEquals(Format.MARKDOWN, options.format)
    }

    @Test
    fun `a user called catalog can still be checked when it comes after another user`() {
        assertEquals(listOf("octocat", "catalog"), parseArgs(listOf("octocat", "catalog")).users)
    }

    @Test
    fun `bad input gives a usage error`() {
        assertFailsWith<UsageException> { parseArgs(listOf("--format", "pdf")) }
        assertFailsWith<UsageException> { parseArgs(listOf("--lang", "de")) }
        assertFailsWith<UsageException> { parseArgs(listOf("--output")) }
        assertFailsWith<UsageException> { parseArgs(listOf("--wat")) }
        assertFailsWith<UsageException> { parseArgs(listOf("a", "b", "--format", "svg")) }
        assertFailsWith<UsageException> { parseArgs(listOf("catalog", "--format", "svg")) }
    }

    @Test
    fun `turkish locale picks turkish`() {
        assertEquals(Lang.TR, Lang.fromEnvironment(mapOf("LANG" to "tr_TR.UTF-8")))
        assertEquals(Lang.EN, Lang.fromEnvironment(mapOf("LANG" to "en_US.UTF-8")))
        assertEquals(Lang.EN, Lang.fromEnvironment(emptyMap()))
    }
}
