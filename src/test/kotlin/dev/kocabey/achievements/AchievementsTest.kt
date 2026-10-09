package dev.kocabey.achievements

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AchievementsTest {

    private val now = Instant.parse("2026-10-09T10:00:00Z")

    private fun stats(
        mergedPrCount: Int = 0,
        topRepoStars: Int = 0,
        acceptedAnswers: Int = 0,
        publicSponsorships: Int = 0,
        closedItems: List<TimedItem> = emptyList(),
        recentMergedPrs: List<MergedPr> = emptyList(),
    ) = UserStats(
        login = "octocat",
        mergedPrCount = mergedPrCount,
        topRepo = "octocat/hello",
        topRepoStars = topRepoStars,
        acceptedAnswers = acceptedAnswers,
        publicSponsorships = publicSponsorships,
        closedItems = closedItems,
        recentMergedPrs = recentMergedPrs,
        isDeveloperProgramMember = true,
        isBountyHunter = false,
        isCampusExpert = false,
        isGitHubStar = false,
    )

    @Test
    fun `pull shark tiers follow the official thresholds`() {
        val cases = mapOf(0 to 0, 1 to 0, 2 to 1, 15 to 1, 16 to 2, 128 to 3, 1024 to 4, 5000 to 4)
        cases.forEach { (count, level) ->
            assertEquals(level, TierProgress(TieredAchievement.PULL_SHARK, count).level, "count=$count")
        }
    }

    @Test
    fun `remaining counts down to the next tier`() {
        val progress = TierProgress(TieredAchievement.STARSTRUCK, 4)
        assertEquals(0, progress.level)
        assertEquals(16, progress.nextThreshold)
        assertEquals(12, progress.remaining)
    }

    @Test
    fun `gold tier has no next threshold`() {
        val progress = TierProgress(TieredAchievement.GALAXY_BRAIN, 40)
        assertEquals(4, progress.level)
        assertNull(progress.nextThreshold)
        assertEquals("x4", progress.label)
    }

    @Test
    fun `quickdraw needs something closed within five minutes`() {
        val slow = TimedItem(now, now.plusSeconds(301))
        val fast = TimedItem(now, now.plusSeconds(300))
        val open = TimedItem(now, null)

        assertFalse(evaluate(stats(closedItems = listOf(slow, open))).quickdraw)
        assertTrue(evaluate(stats(closedItems = listOf(slow, fast))).quickdraw)
    }

    @Test
    fun `yolo needs a self merged PR without reviews`() {
        val reviewed = MergedPr(mergedBy = "octocat", reviewCount = 1)
        val mergedBySomeoneElse = MergedPr(mergedBy = "hubot", reviewCount = 0)
        val yolo = MergedPr(mergedBy = "OctoCat", reviewCount = 0)

        assertFalse(evaluate(stats(recentMergedPrs = listOf(reviewed, mergedBySomeoneElse))).yolo)
        assertTrue(evaluate(stats(recentMergedPrs = listOf(yolo))).yolo)
    }

    @Test
    fun `public sponsor counts public sponsorships`() {
        assertFalse(evaluate(stats()).publicSponsor)
        assertTrue(evaluate(stats(publicSponsorships = 1)).publicSponsor)
    }
}
