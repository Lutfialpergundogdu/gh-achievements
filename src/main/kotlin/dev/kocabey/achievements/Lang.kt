package dev.kocabey.achievements

enum class Lang(val code: String) {
    EN("en"),
    TR("tr"),
    ;

    companion object {
        fun fromCode(code: String): Lang? = entries.firstOrNull { it.code.equals(code, ignoreCase = true) }

        /** Picks Turkish when the shell locale is Turkish, English otherwise. */
        fun fromEnvironment(env: Map<String, String> = System.getenv()): Lang {
            val locale = listOf("LC_ALL", "LC_MESSAGES", "LANG").firstNotNullOfOrNull { env[it]?.takeIf(String::isNotBlank) }
            return if (locale?.lowercase()?.startsWith("tr") == true) TR else EN
        }
    }
}

data class Localized(val en: String, val tr: String) {
    operator fun get(lang: Lang): String = when (lang) {
        Lang.EN -> en
        Lang.TR -> tr
    }
}
