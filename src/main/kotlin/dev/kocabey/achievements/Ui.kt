package dev.kocabey.achievements

/** User-facing strings for the report and catalog views. */
class Ui(val lang: Lang) {
    private fun s(en: String, tr: String) = Localized(en, tr)[lang]

    fun header(login: String) = s("GitHub achievements for @$login", "@$login için GitHub rozetleri")

    val tiered = s("Tiered", "Kademeli")
    val oneTime = s("One-time", "Tek seferlik")
    val highlights = s("Profile highlights", "Profil rozetleri")
    val maxedOut = s("maxed out", "tamamlandı")
    val notTrackable = s("not available through the API, check your profile", "API'den okunamıyor, profilinden kontrol et")

    fun unlocksAt(threshold: Int, remaining: Int) =
        s("unlocks at $threshold ($remaining to go)", "$threshold olunca açılır ($remaining kaldı)")

    fun nextTier(label: String, nextLevel: Int, threshold: Int, remaining: Int) =
        s("$label, x$nextLevel at $threshold ($remaining to go)", "$label, x$nextLevel için $threshold ($remaining kaldı)")

    fun tierLabel(progress: TierProgress) = when (progress.level) {
        0 -> s("locked", "kilitli")
        1 -> s("unlocked", "açıldı")
        else -> "x${progress.level}"
    }

    val oneTimeHint = mapOf(
        "quickdraw" to s("closed an issue or PR within 5 minutes", "bir issue ya da PR'ı 5 dakika içinde kapattı"),
        "yolo" to s("merged a PR without a review", "review almadan PR merge etti"),
        "public-sponsor" to s("sponsors someone publicly", "herkese açık olarak birine sponsor"),
    )

    val footnote = s(
        "Quickdraw and YOLO only look at the latest 100 PRs/issues. Private activity only shows up for your own account.",
        "Quickdraw ve YOLO sadece son 100 PR/issue'ya bakar. Private aktivite sadece kendi hesabın için görünür.",
    )

    // Catalog view
    val catalogTitle = s("GitHub profile achievements", "GitHub profil rozetleri")
    val earnable = s("Earnable", "Kazanılabilir")
    val retired = s("Retired", "Artık verilmiyor")
    val disabled = s("Disabled for now", "Şimdilik kapalı")
    val internal = s("GitHub staff only", "Sadece GitHub çalışanları")
    val howToEarn = s("How to earn", "Nasıl kazanılır")
    val tip = s("Tip", "İpucu")
    val tiers = s("Tiers", "Kademeler")
    val tracked = s("Tracked by this tool", "Bu araç takip ediyor")
    val yes = s("yes", "evet")
    val no = s("no", "hayır")
    val faq = s("FAQ", "Sık sorulan sorular")

    fun availability(a: Availability) = when (a) {
        Availability.EARNABLE -> earnable
        Availability.RETIRED -> retired
        Availability.DISABLED -> disabled
        Availability.INTERNAL -> internal
    }

    fun category(c: Category) = when (c) {
        Category.TIERED -> tiered
        Category.ONE_TIME -> oneTime
        Category.HIGHLIGHT -> highlights
    }
}
