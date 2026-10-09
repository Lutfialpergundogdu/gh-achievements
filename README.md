# gh-achievements

A small Kotlin CLI that shows how close you are to the next GitHub profile achievement.

GitHub shows the badges you already have, but not how far you are from the next tier. This tool asks the GitHub GraphQL API and tells you, for example, that you need 12 more merged PRs for Pull Shark x2.

```
GitHub achievements for @muhammedalikcb

Tiered
  Pull Shark     ██░░░░░░░░  4 merged PRs               unlocked, x2 at 16 (12 to go)
  Starstruck     ██░░░░░░░░  4 stars on one repo        unlocks at 16 (12 to go)  muhammedalikcb/securecheck
  Galaxy Brain   ░░░░░░░░░░  0 accepted answers         unlocks at 2 (2 to go)

One-time
  ✔ Quickdraw            closed an issue or PR within 5 minutes
  ✔ YOLO                 merged a PR without a review
  ✔ Public Sponsor       sponsors someone publicly
  ? Pair Extraordinaire  not available through the API, check your profile

Profile highlights
  ✔ Developer Program Member
  ✘ Security Bug Bounty Hunter
  ✘ GitHub Campus Expert
  ✘ GitHub Star
```

## Usage

You need JDK 17+ and a GitHub token. If you use the [GitHub CLI](https://cli.github.com/) and you're logged in, there's nothing to set up. Otherwise export `GITHUB_TOKEN` (a token with no extra scopes is enough for public data).

```bash
git clone https://github.com/muhammedalikcb/gh-achievements.git
cd gh-achievements
./gradlew installDist

build/install/gh-achievements/bin/gh-achievements            # your own account
build/install/gh-achievements/bin/gh-achievements torvalds   # anyone else
build/install/gh-achievements/bin/gh-achievements --json     # for scripts
```

`--no-color` turns off colors (so does the `NO_COLOR` env variable).

## What it checks

| Achievement | How it's counted | Tiers |
| --- | --- | --- |
| Pull Shark | merged pull requests you opened | 2, 16, 128, 1024 |
| Starstruck | stars on your most starred repo (forks don't count) | 16, 128, 512, 4096 |
| Galaxy Brain | discussion answers marked as accepted | 2, 8, 16, 32 |
| Quickdraw | an issue or PR closed within 5 minutes of opening | |
| YOLO | a PR you merged yourself without any review | |
| Public Sponsor | at least one public sponsorship | |

It also shows the profile highlights the API exposes: Developer Program Member, Security Bug Bounty Hunter, Campus Expert and GitHub Star.

## Limitations

- Pair Extraordinaire can't be counted. The API doesn't expose co-authored commits per user.
- Quickdraw and YOLO only look at your latest 100 PRs and issues.
- When you check someone else, only their public activity is visible, so the numbers can be lower than what their profile shows.
- GitHub doesn't publish the exact rules. The thresholds here come from the community list at [Schweinepriester/github-profile-achievements](https://github.com/Schweinepriester/github-profile-achievements).

## License

MIT
