# Contributing

Thanks for wanting to help! Türkçe yazmak istersen o da olur.

## Running it locally

```bash
./gradlew test          # all tests
./gradlew installDist   # build the CLI into build/install/gh-achievements
```

## Changing achievement texts or rules

Everything about achievements (tiers, descriptions, tips, FAQ) lives in
`src/main/kotlin/dev/kocabey/achievements/Catalog.kt`. The guides in `docs/` are generated from it,
so after editing the catalog run:

```bash
./gradlew installDist
build/install/gh-achievements/bin/gh-achievements catalog --format markdown --lang en --output docs/ACHIEVEMENTS.md
build/install/gh-achievements/bin/gh-achievements catalog --format markdown --lang tr --output docs/ACHIEVEMENTS.tr.md
```

A test fails if the docs and the catalog get out of sync.

## Adding a language

1. Add it to `Lang` in `Lang.kt` and a field to `Localized`.
2. Fill in the new field in `Catalog.kt` and `Ui.kt`. The compiler will point at every place that needs it.
3. Generate `docs/ACHIEVEMENTS.<code>.md` and add a test case.

## Pull requests

Keep them small and focused, and add a test when you change behavior.
