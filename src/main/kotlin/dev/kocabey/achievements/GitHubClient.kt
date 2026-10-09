package dev.kocabey.achievements

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant

class GitHubException(message: String) : Exception(message)

class GitHubClient(private val token: String) {

    private val http = HttpClient.newHttpClient()

    fun fetchStats(login: String): UserStats {
        val user = query(USER_QUERY, login)["user"]
            ?.takeUnless { it is JsonNull }
            ?.jsonObject
            ?: throw GitHubException("User '$login' not found")

        val topRepo = user.obj("repositories").array("nodes").firstOrNull()?.jsonObject
        val mergedPrs = user.obj("mergedPrs")

        return UserStats(
            login = user.string("login")!!,
            mergedPrCount = mergedPrs.int("totalCount"),
            topRepo = topRepo?.string("nameWithOwner"),
            topRepoStars = topRepo?.int("stargazerCount") ?: 0,
            acceptedAnswers = user.obj("repositoryDiscussionComments").int("totalCount"),
            publicSponsorships = user.obj("sponsoring").int("totalCount"),
            closedItems = (user.obj("closedPrs").array("nodes") + user.obj("closedIssues").array("nodes"))
                .map { it.jsonObject }
                .map { TimedItem(Instant.parse(it.string("createdAt")), it.string("closedAt")?.let(Instant::parse)) },
            recentMergedPrs = mergedPrs.array("nodes").map { it.jsonObject }.map {
                MergedPr(
                    mergedBy = it["mergedBy"]?.takeUnless { m -> m is JsonNull }?.jsonObject?.string("login"),
                    reviewCount = it.obj("reviews").int("totalCount"),
                )
            },
            isDeveloperProgramMember = user.bool("isDeveloperProgramMember"),
            isBountyHunter = user.bool("isBountyHunter"),
            isCampusExpert = user.bool("isCampusExpert"),
            isGitHubStar = user.bool("isGitHubStar"),
        )
    }

    /** Login of the account the token belongs to, used when no username is passed. */
    fun fetchViewerLogin(): String = query(VIEWER_QUERY).obj("viewer").string("login")!!

    private fun query(query: String, login: String? = null): JsonObject {
        val body = buildJsonObject {
            put("query", query)
            if (login != null) put("variables", buildJsonObject { put("login", login) })
        }
        val request = HttpRequest.newBuilder(URI.create("https://api.github.com/graphql"))
            .header("Authorization", "Bearer $token")
            .header("User-Agent", "gh-achievements")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build()

        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 401) throw GitHubException("GitHub rejected the token (401). Check GITHUB_TOKEN or run `gh auth login`.")
        if (response.statusCode() != 200) throw GitHubException("GitHub API returned HTTP ${response.statusCode()}")

        val json = Json.parseToJsonElement(response.body()).jsonObject
        json["errors"]?.jsonArray?.firstOrNull()?.let {
            throw GitHubException(it.jsonObject.string("message") ?: "Unknown GraphQL error")
        }
        return json.obj("data")
    }

    companion object {
        /** Reads GITHUB_TOKEN / GH_TOKEN, then falls back to the GitHub CLI if it's installed and logged in. */
        fun resolveToken(): String? {
            listOf("GITHUB_TOKEN", "GH_TOKEN").firstNotNullOfOrNull { System.getenv(it)?.takeIf(String::isNotBlank) }
                ?.let { return it }

            return runCatching {
                val process = ProcessBuilder("gh", "auth", "token").redirectErrorStream(true).start()
                val output = process.inputStream.bufferedReader().readText().trim()
                output.takeIf { process.waitFor() == 0 && it.isNotEmpty() }
            }.getOrNull()
        }

        private const val VIEWER_QUERY = "query { viewer { login } }"

        private val USER_QUERY = """
            query(${'$'}login: String!) {
              user(login: ${'$'}login) {
                login
                isDeveloperProgramMember
                isBountyHunter
                isCampusExpert
                isGitHubStar
                sponsoring { totalCount }
                repositoryDiscussionComments(onlyAnswers: true) { totalCount }
                repositories(ownerAffiliations: OWNER, isFork: false, first: 1, orderBy: {field: STARGAZERS, direction: DESC}) {
                  nodes { nameWithOwner stargazerCount }
                }
                mergedPrs: pullRequests(states: MERGED, first: 100, orderBy: {field: CREATED_AT, direction: DESC}) {
                  totalCount
                  nodes { mergedBy { login } reviews { totalCount } }
                }
                closedPrs: pullRequests(states: [CLOSED, MERGED], first: 100, orderBy: {field: CREATED_AT, direction: DESC}) {
                  nodes { createdAt closedAt }
                }
                closedIssues: issues(states: CLOSED, first: 100, orderBy: {field: CREATED_AT, direction: DESC}) {
                  nodes { createdAt closedAt }
                }
              }
            }
        """.trimIndent()
    }
}

private fun JsonObject.obj(key: String): JsonObject = getValue(key).jsonObject
private fun JsonObject.array(key: String): List<JsonElement> = getValue(key).jsonArray
private fun JsonObject.int(key: String): Int = getValue(key).jsonPrimitive.int
private fun JsonObject.bool(key: String): Boolean = getValue(key).jsonPrimitive.content.toBoolean()
private fun JsonObject.string(key: String): String? = get(key)?.takeUnless { it is JsonNull }?.jsonPrimitive?.content
