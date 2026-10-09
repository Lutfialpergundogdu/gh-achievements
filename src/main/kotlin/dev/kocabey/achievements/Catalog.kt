package dev.kocabey.achievements

enum class Category { TIERED, ONE_TIME, HIGHLIGHT }

enum class Availability { EARNABLE, RETIRED, DISABLED, INTERNAL }

data class CatalogEntry(
    val id: String,
    val title: String,
    val category: Category,
    val availability: Availability,
    /** Whether this tool can read it from the GitHub API. */
    val trackable: Boolean,
    val howToEarn: Localized,
    val tip: Localized? = null,
    val thresholds: List<Int> = emptyList(),
)

data class FaqItem(val question: Localized, val answer: Localized)

/**
 * Everything the tool and the docs know about achievements and profile highlights.
 * docs/ACHIEVEMENTS*.md are generated from this file, so edit here and regenerate.
 */
object Catalog {

    val entries: List<CatalogEntry> = listOf(
        CatalogEntry(
            id = "pull-shark",
            title = "Pull Shark",
            category = Category.TIERED,
            availability = Availability.EARNABLE,
            trackable = true,
            thresholds = TieredAchievement.PULL_SHARK.thresholds,
            howToEarn = Localized(
                en = "Open pull requests that get merged. PRs in your own repositories count too.",
                tr = "Açtığın pull request'lerin merge edilmesi yeterli. Kendi repolarındaki PR'lar da sayılır.",
            ),
            tip = Localized(
                en = "Look for small fixes in projects you already use: broken links, outdated docs, typos in examples, issues labeled \"good first issue\".",
                tr = "Zaten kullandığın projelerde küçük düzeltmeler ara: kırık linkler, eskimiş dokümanlar, örneklerdeki hatalar, \"good first issue\" etiketli işler.",
            ),
        ),
        CatalogEntry(
            id = "starstruck",
            title = "Starstruck",
            category = Category.TIERED,
            availability = Availability.EARNABLE,
            trackable = true,
            thresholds = TieredAchievement.STARSTRUCK.thresholds,
            howToEarn = Localized(
                en = "Create a repository that reaches the star count. It has to be a single repo you own, forks don't count.",
                tr = "Oluşturduğun bir repo bu yıldız sayısına ulaşmalı. Tek bir repo olmalı ve fork'lar sayılmaz.",
            ),
            tip = Localized(
                en = "A clear README with a screenshot or GIF, a license and a one-line install command go a long way. Then share it where your audience actually is.",
                tr = "Ekran görüntüsü ya da GIF içeren net bir README, bir lisans ve tek satırlık kurulum komutu çok fark yaratır. Sonra hedef kitlenin olduğu yerde paylaş.",
            ),
        ),
        CatalogEntry(
            id = "galaxy-brain",
            title = "Galaxy Brain",
            category = Category.TIERED,
            availability = Availability.EARNABLE,
            trackable = true,
            thresholds = TieredAchievement.GALAXY_BRAIN.thresholds,
            howToEarn = Localized(
                en = "Answer questions in GitHub Discussions and get your answers marked as accepted.",
                tr = "GitHub Discussions'taki soruları cevapla ve cevaplarının \"kabul edildi\" olarak işaretlenmesini sağla.",
            ),
            tip = Localized(
                en = "Answers in github.com/orgs/community don't count since February 2024. Pick repos where maintainers actually mark answers, and back your answer with docs or source links.",
                tr = "Şubat 2024'ten beri github.com/orgs/community'deki cevaplar sayılmıyor. Maintainer'ların cevapları gerçekten işaretlediği repoları seç ve cevabını doküman ya da kaynak kod linkiyle destekle.",
            ),
        ),
        CatalogEntry(
            id = "pair-extraordinaire",
            title = "Pair Extraordinaire",
            category = Category.TIERED,
            availability = Availability.EARNABLE,
            trackable = false,
            thresholds = listOf(1, 10, 24, 48),
            howToEarn = Localized(
                en = "Be a co-author on a commit in a merged pull request.",
                tr = "Merge edilen bir pull request'teki bir commit'te co-author olarak yer al.",
            ),
            tip = Localized(
                en = "The easiest real way: leave a suggestion in someone's PR review. When they click \"Commit suggestion\", GitHub adds you as co-author. The email in a Co-authored-by line has to be linked to your account.",
                tr = "En kolay gerçek yol: birinin PR'ında review yaparken öneri (suggestion) bırak. \"Commit suggestion\"a bastığında GitHub seni otomatik co-author yapar. Co-authored-by satırındaki e-posta hesabına bağlı olmalı.",
            ),
        ),
        CatalogEntry(
            id = "quickdraw",
            title = "Quickdraw",
            category = Category.ONE_TIME,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Close an issue or a pull request within 5 minutes of opening it.",
                tr = "Bir issue ya da pull request'i açtıktan sonraki 5 dakika içinde kapat.",
            ),
            tip = Localized(
                en = "Happens naturally when you open an issue and immediately fix it with a PR that says \"Closes #N\".",
                tr = "Bir issue açıp hemen \"Closes #N\" yazan bir PR ile düzelttiğinde kendiliğinden gelir.",
            ),
        ),
        CatalogEntry(
            id = "yolo",
            title = "YOLO",
            category = Category.ONE_TIME,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Merge a pull request without a code review.",
                tr = "Bir pull request'i code review almadan merge et.",
            ),
            tip = Localized(
                en = "Any PR you merge yourself in a repo without required reviews does it.",
                tr = "Zorunlu review olmayan bir repoda kendi PR'ını merge etmen yeterli.",
            ),
        ),
        CatalogEntry(
            id = "public-sponsor",
            title = "Public Sponsor",
            category = Category.ONE_TIME,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Sponsor someone through GitHub Sponsors with your sponsorship visible to everyone.",
                tr = "GitHub Sponsors üzerinden birine sponsor ol ve sponsorluğun herkese görünür olsun.",
            ),
            tip = Localized(
                en = "A one-time sponsorship works too. Many maintainers accept a custom amount, so it doesn't have to be expensive. Keep \"Who can see your sponsorship?\" on Everyone.",
                tr = "Tek seferlik sponsorluk da sayılıyor. Çoğu maintainer serbest tutar kabul ediyor, yani pahalı olması gerekmiyor. \"Who can see your sponsorship?\" ayarı Everyone kalsın.",
            ),
        ),
        CatalogEntry(
            id = "arctic-code-vault-contributor",
            title = "Arctic Code Vault Contributor",
            category = Category.ONE_TIME,
            availability = Availability.RETIRED,
            trackable = false,
            howToEarn = Localized(
                en = "Given to people who contributed to repositories in the 2020 GitHub Archive Program. Can't be earned anymore.",
                tr = "2020 GitHub Archive Program'daki repolara katkı verenlere verildi. Artık kazanılamıyor.",
            ),
        ),
        CatalogEntry(
            id = "mars-2020-contributor",
            title = "Mars 2020 Contributor",
            category = Category.ONE_TIME,
            availability = Availability.RETIRED,
            trackable = false,
            howToEarn = Localized(
                en = "Given to contributors of the open source projects used in the Mars 2020 helicopter mission. Can't be earned anymore.",
                tr = "Mars 2020 helikopter görevinde kullanılan açık kaynak projelere katkı verenlere verildi. Artık kazanılamıyor.",
            ),
        ),
        CatalogEntry(
            id = "heart-on-your-sleeve",
            title = "Heart On Your Sleeve",
            category = Category.TIERED,
            availability = Availability.DISABLED,
            trackable = false,
            howToEarn = Localized(
                en = "Reacting with a heart emoji. It was tested for a while but isn't active right now.",
                tr = "Kalp emojisiyle tepki vermek. Bir süre test edildi ama şu an aktif değil.",
            ),
        ),
        CatalogEntry(
            id = "open-sourcerer",
            title = "Open Sourcerer",
            category = Category.TIERED,
            availability = Availability.DISABLED,
            trackable = false,
            howToEarn = Localized(
                en = "Having pull requests merged in several public repositories. It was tested for a while but isn't active right now.",
                tr = "Birden fazla public repoda pull request'lerin merge edilmesi. Bir süre test edildi ama şu an aktif değil.",
            ),
        ),
        CatalogEntry(
            id = "proxima",
            title = "Proxima Pioneer / Staffshipper / Staffuser",
            category = Category.ONE_TIME,
            availability = Availability.INTERNAL,
            trackable = false,
            howToEarn = Localized(
                en = "Internal badges for GitHub staff who worked on GitHub itself.",
                tr = "GitHub'ın kendisi üzerinde çalışan GitHub çalışanlarına verilen iç rozetler.",
            ),
        ),
        CatalogEntry(
            id = "pro",
            title = "PRO",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = false,
            howToEarn = Localized(
                en = "Use a GitHub Pro plan.",
                tr = "GitHub Pro planı kullan.",
            ),
            tip = Localized(
                en = "Students get Pro for free through the GitHub Student Developer Pack (education.github.com/pack). Graduate students count too.",
                tr = "Öğrenciler GitHub Student Developer Pack ile Pro'yu ücretsiz alıyor (education.github.com/pack). Yüksek lisans öğrencileri de dahil.",
            ),
        ),
        CatalogEntry(
            id = "developer-program-member",
            title = "Developer Program Member",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Register for the GitHub Developer Program while building something that uses the GitHub API.",
                tr = "GitHub API'sini kullanan bir şey geliştirirken GitHub Developer Program'a kaydol.",
            ),
            tip = Localized(
                en = "Register at github.com/developer/register with a support email and a website for your integration.",
                tr = "github.com/developer/register adresinden bir destek e-postası ve entegrasyonunun web sitesiyle kaydol.",
            ),
        ),
        CatalogEntry(
            id = "security-advisory-credit",
            title = "Security advisory credit",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = false,
            howToEarn = Localized(
                en = "Get a contribution to the GitHub Advisory Database accepted.",
                tr = "GitHub Advisory Database'e yaptığın bir katkının kabul edilmesi.",
            ),
            tip = Localized(
                en = "You don't need to find a new vulnerability. Improving an existing advisory (missing CWE, affected versions, fix commit) through \"Suggest improvements\" or a PR to github/advisory-database counts.",
                tr = "Yeni bir açık bulman gerekmiyor. Var olan bir kaydı iyileştirmek de sayılıyor: eksik CWE, etkilenen sürümler ya da düzeltme commit'i. \"Suggest improvements\" ile ya da github/advisory-database'e PR açarak yapılıyor.",
            ),
        ),
        CatalogEntry(
            id = "bug-bounty-hunter",
            title = "Security Bug Bounty Hunter",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Report a valid vulnerability to the GitHub Security Bug Bounty program.",
                tr = "GitHub Security Bug Bounty programına geçerli bir güvenlik açığı bildir.",
            ),
            tip = Localized(
                en = "Scope and rules are at bounty.github.com. The GitHub mobile apps are in scope too.",
                tr = "Kapsam ve kurallar bounty.github.com'da. GitHub mobil uygulamaları da kapsamda.",
            ),
        ),
        CatalogEntry(
            id = "campus-expert",
            title = "GitHub Campus Expert",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Take part in the GitHub Campus Expert program as a student community leader.",
                tr = "Öğrenci topluluk lideri olarak GitHub Campus Expert programına katıl.",
            ),
        ),
        CatalogEntry(
            id = "github-star",
            title = "GitHub Star",
            category = Category.HIGHLIGHT,
            availability = Availability.EARNABLE,
            trackable = true,
            howToEarn = Localized(
                en = "Get nominated and selected for the GitHub Stars program, which recognizes people who share knowledge with the community.",
                tr = "Toplulukla bilgi paylaşan kişileri öne çıkaran GitHub Stars programına aday gösterilip seçil.",
            ),
            tip = Localized(
                en = "Talks, blog posts, videos and podcasts about open source are what the program looks for. Nominations are at stars.github.com.",
                tr = "Programın aradığı şey açık kaynak üzerine konuşmalar, blog yazıları, videolar ve podcast'ler. Adaylık stars.github.com üzerinden.",
            ),
        ),
    )

    val faq: List<FaqItem> = listOf(
        FaqItem(
            Localized("I did the thing but the badge isn't showing.", "Gerekeni yaptım ama rozet görünmüyor."),
            Localized(
                en = "It can take a few minutes, sometimes longer. Also check Settings > Public profile > Contributions & activity: achievements can be hidden there, and private contributions only count when \"Include private contributions on my profile\" is on.",
                tr = "Birkaç dakika, bazen daha uzun sürebiliyor. Ayrıca Settings > Public profile > Contributions & activity bölümüne bak: rozetler orada gizlenebiliyor ve private katkılar sadece \"Include private contributions on my profile\" açıkken sayılıyor.",
            ),
        ),
        FaqItem(
            Localized("My commits don't count.", "Commit'lerim sayılmıyor."),
            Localized(
                en = "The commit email has to be added and verified on your GitHub account. Check it with `git log --format='%ae'` and compare it with github.com/settings/emails.",
                tr = "Commit'teki e-posta GitHub hesabına eklenmiş ve doğrulanmış olmalı. `git log --format='%ae'` ile kontrol edip github.com/settings/emails sayfasıyla karşılaştır.",
            ),
        ),
        FaqItem(
            Localized("Can I farm badges with a second account?", "İkinci bir hesapla rozet kasabilir miyim?"),
            Localized(
                en = "GitHub's terms allow one free personal account per person, so a second account just for badges breaks the rules. You don't need it anyway: every badge here can be earned with real work.",
                tr = "GitHub'ın kullanım şartları kişi başına bir ücretsiz kişisel hesaba izin veriyor, yani sadece rozet için açılan ikinci bir hesap kurallara aykırı. Zaten gerek de yok: buradaki her rozet gerçek işle kazanılabiliyor.",
            ),
        ),
        FaqItem(
            Localized("Where do these rules come from?", "Bu kurallar nereden geliyor?"),
            Localized(
                en = "GitHub doesn't publish the exact criteria. The thresholds come from what the community has observed, mainly the list at github.com/Schweinepriester/github-profile-achievements. If something changes, please open an issue.",
                tr = "GitHub kesin kriterleri yayınlamıyor. Eşikler topluluğun gözlemlerinden geliyor, özellikle github.com/Schweinepriester/github-profile-achievements listesinden. Bir şey değişirse lütfen issue aç.",
            ),
        ),
    )

    fun byId(id: String): CatalogEntry = entries.first { it.id == id }
}
