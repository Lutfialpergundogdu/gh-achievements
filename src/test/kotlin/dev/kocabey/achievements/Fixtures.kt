package dev.kocabey.achievements

import java.time.Instant

fun sampleStats(
    login: String = "octocat",
    mergedPrCount: Int = 4,
    topRepoStars: Int = 4,
    acceptedAnswers: Int = 0,
    publicSponsorships: Int = 1,
    closedItems: List<TimedItem> = listOf(TimedItem(Instant.parse("2026-10-09T10:00:00Z"), Instant.parse("2026-10-09T10:00:08Z"))),
    recentMergedPrs: List<MergedPr> = listOf(MergedPr(mergedBy = login, reviewCount = 0)),
    isDeveloperProgramMember: Boolean = true,
) = UserStats(
    login = login,
    mergedPrCount = mergedPrCount,
    topRepo = "$login/hello",
    topRepoStars = topRepoStars,
    acceptedAnswers = acceptedAnswers,
    publicSponsorships = publicSponsorships,
    closedItems = closedItems,
    recentMergedPrs = recentMergedPrs,
    isDeveloperProgramMember = isDeveloperProgramMember,
    isBountyHunter = false,
    isCampusExpert = false,
    isGitHubStar = false,
)
