# gh-achievements

[![Build](https://github.com/muhammedalikcb/gh-achievements/actions/workflows/build.yml/badge.svg)](https://github.com/muhammedalikcb/gh-achievements/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)

**English** · [Türkçe](README.tr.md)

GitHub shows the achievements you already have, but not how far you are from the next one. `gh-achievements` asks the GitHub API and tells you, for example, that you need 12 more merged PRs for Pull Shark x2, along with a tip on how to get there.

<img src="docs/example-card.svg" alt="Example achievements card" width="495">

- **CLI** with a colored terminal report, plus JSON and Markdown output
- **SVG card** for your profile README that follows light and dark mode
- **GitHub Action** that keeps the card up to date every day
- **Full achievements guide** with tiers, how to earn each one, tips and a FAQ ([docs/ACHIEVEMENTS.md](docs/ACHIEVEMENTS.md))
- **English and Turkish**, picked from your locale or with `--lang`

## Quick start

You need JDK 17+ and a GitHub token. If you use the [GitHub CLI](https://cli.github.com/) and you're logged in, there's nothing else to set up. Otherwise export `GITHUB_TOKEN` (a token without extra scopes is enough for public data).

```bash
git clone https://github.com/muhammedalikcb/gh-achievements.git
cd gh-achievements
./gradlew installDist
alias gh-achievements="$PWD/build/install/gh-achievements/bin/gh-achievements"

gh-achievements                 # your own account
gh-achievements torvalds        # anyone else
gh-achievements catalog         # every achievement and how to earn it
```

```
GitHub achievements for @muhammedalikcb

Tiered
  Pull Shark     ██░░░░░░░░  4 merged PRs               unlocked, x2 at 16 (12 to go)
    → Look for small fixes in projects you already use: broken links, outdated docs, ...
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
  ...
```

## Options

| Option | What it does |
| --- | --- |
| `-f, --format text\|json\|markdown\|svg` | Output format, `text` by default |
| `--json` | Short for `--format json` |
| `-l, --lang en\|tr` | Language, defaults to your locale |
| `-o, --output <file>` | Write to a file instead of the terminal |
| `--theme auto\|light\|dark` | SVG theme, `auto` follows the viewer |
| `--no-tips` | Hide the "how to get the next tier" hints |
| `--no-color` | Plain output (`NO_COLOR` works too) |

You can pass several usernames to compare them. The catalog works with `--format markdown` and `--format json` too, which is how the docs in this repo are generated.

## Achievements card on your profile

Add [examples/profile-card.yml](examples/profile-card.yml) to your profile repository as `.github/workflows/achievements.yml`:

```yaml
- uses: muhammedalikcb/gh-achievements@v0.2.0
  with:
    output: achievements.svg
    # lang: tr
    # theme: dark
```

Then put `![My GitHub achievements](achievements.svg)` in your README. The workflow runs every morning and only commits when something changed.

| Input | Default | |
| --- | --- | --- |
| `username` | repository owner | Who to check |
| `output` | `achievements.svg` | File to write |
| `format` | `svg` | `svg`, `markdown` or `json` |
| `lang` | `en` | `en` or `tr` |
| `theme` | `auto` | `auto`, `light` or `dark` |
| `token` | `github.token` | Token for the GitHub API |

## What it checks

| | Counted from | Tiers |
| --- | --- | --- |
| Pull Shark | merged pull requests you opened | 2, 16, 128, 1024 |
| Starstruck | stars on your most starred repo (forks don't count) | 16, 128, 512, 4096 |
| Galaxy Brain | discussion answers marked as accepted | 2, 8, 16, 32 |
| Quickdraw | an issue or PR closed within 5 minutes | |
| YOLO | a PR you merged yourself without a review | |
| Public Sponsor | at least one public sponsorship | |
| Highlights | Developer Program, Bug Bounty Hunter, Campus Expert, GitHub Star | |

The [achievements guide](docs/ACHIEVEMENTS.md) also covers the ones the API can't see (Pair Extraordinaire, PRO, Security advisory credit) and the retired ones.

## Limitations

- Pair Extraordinaire can't be counted because the API doesn't expose co-authored commits per user.
- Quickdraw and YOLO only look at the latest 100 PRs and issues.
- For other people only public activity is visible, so their numbers can be lower than what their profile shows.
- GitHub doesn't publish the exact rules. The thresholds come from what the community has observed, mainly [Schweinepriester/github-profile-achievements](https://github.com/Schweinepriester/github-profile-achievements). If something changed, an issue is very welcome.

## Contributing

Bug reports, translations and new checks are welcome, see [CONTRIBUTING.md](CONTRIBUTING.md). All achievement texts live in [`Catalog.kt`](src/main/kotlin/dev/kocabey/achievements/Catalog.kt), so that's the place to fix a description or add a language.

## License

[MIT](LICENSE)
