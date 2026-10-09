package dev.kocabey.achievements

import java.time.Duration
import java.time.Instant

/** Thresholds for tiered achievements: base, x2 (bronze), x3 (silver), x4 (gold). */
enum class TieredAchievement(val id: String, val title: String, val unit: Localized, val thresholds: List<Int>) {
    PULL_SHARK("pull-shark", "Pull Shark", Localized("merged PRs", "merge edilmiş PR"), listOf(2, 16, 128, 1024)),
    STARSTRUCK("starstruck", "Starstruck", Localized("stars on one repo", "yıldız (tek repoda)"), listOf(16, 128, 512, 4096)),
    GALAXY_BRAIN("galaxy-brain", "Galaxy Brain", Localized("accepted answers", "kabul edilen cevap"), listOf(2, 8, 16, 32)),
}

data class TierProgress(
    val achievement: TieredAchievement,
    val count: Int,
) {
    /** 0 = not earned yet, 1 = base, 2 = x2, 3 = x3, 4 = x4. */
    val level: Int = achievement.thresholds.count { count >= it }

    val nextThreshold: Int? = achievement.thresholds.getOrNull(level)

    val remaining: Int? = nextThreshold?.let { it - count }

    val label: String = when (level) {
        0 -> "locked"
        1 -> "unlocked"
        else -> "x$level"
    }
}

data class TimedItem(val createdAt: Instant, val closedAt: Instant?)

data class MergedPr(val mergedBy: String?, val reviewCount: Int)

data class UserStats(
    val login: String,
    val mergedPrCount: Int,
    val topRepo: String?,
    val topRepoStars: Int,
    val acceptedAnswers: Int,
    val publicSponsorships: Int,
    val closedItems: List<TimedItem>,
    val recentMergedPrs: List<MergedPr>,
    val isDeveloperProgramMember: Boolean,
    val isBountyHunter: Boolean,
    val isCampusExpert: Boolean,
    val isGitHubStar: Boolean,
)

data class Report(
    val tiers: List<TierProgress>,
    val quickdraw: Boolean,
    val yolo: Boolean,
    val publicSponsor: Boolean,
    /** Catalog id to whether the user has it. Only highlights the API exposes. */
    val highlights: Map<String, Boolean>,
) {
    /** Catalog id to earned state for the one-time achievements this tool can check. */
    val oneTime: Map<String, Boolean> = linkedMapOf("quickdraw" to quickdraw, "yolo" to yolo, "public-sponsor" to publicSponsor)
}

private val QUICKDRAW_WINDOW: Duration = Duration.ofMinutes(5)

fun evaluate(stats: UserStats): Report = Report(
    tiers = listOf(
        TierProgress(TieredAchievement.PULL_SHARK, stats.mergedPrCount),
        TierProgress(TieredAchievement.STARSTRUCK, stats.topRepoStars),
        TierProgress(TieredAchievement.GALAXY_BRAIN, stats.acceptedAnswers),
    ),
    quickdraw = stats.closedItems.any { it.closedAt != null && Duration.between(it.createdAt, it.closedAt) <= QUICKDRAW_WINDOW },
    yolo = stats.recentMergedPrs.any { it.mergedBy.equals(stats.login, ignoreCase = true) && it.reviewCount == 0 },
    publicSponsor = stats.publicSponsorships > 0,
    highlights = linkedMapOf(
        "developer-program-member" to stats.isDeveloperProgramMember,
        "bug-bounty-hunter" to stats.isBountyHunter,
        "campus-expert" to stats.isCampusExpert,
        "github-star" to stats.isGitHubStar,
    ),
)
