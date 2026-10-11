# Mob Scores working rules

Start with [CONTRIBUTING.md](CONTRIBUTING.md) for onboarding, architecture,
builds, testing, and script safety. [README.md](README.md) is the user-facing
behavior reference.

- Use **Mob Scores** in human-facing prose. Preserve machine-facing identifiers
  such as the `MobScores` repository/plugin data folder, Java package and class
  names, `mob-scores` bucket ID, command names, and URLs.
- Inspect the current diff before editing; preserve unrelated work. Edit source
  rather than generated `build/`, `bin/`, or `.gradle/` output. Do not read,
  expose, or commit `.env`, server/player data, or publishing tokens.
- Keep documentation aligned with the implementation: Paper 26.2/26.3 only,
  ScoreKeeper 0.2.2+, no Mob Scores commands or permissions, and no current
  Java behavior tests. Keep manual scenarios distinct from automated tests.
- When editing Java, preserve the AGPL header and use the existing Gradle
  Spotless formatter. Keep Bukkit/Paper state and API access on the main
  thread. Avoid unrelated changes to the gameplay and scoring contract.
- Before editing `docs/project-description.json`, read
  `docs/projects.schema.json`. Keep it schema-valid and preserve exactly the
  existing top-level project key, `id`, `title`, `docId`, `image.url`, and
  `headerImage.url` when present. Preserve `headerImage: null` or its absence.
  These are consuming-project integration values: do not derive replacements
  from repository names or “correct” their spelling, capitalization, spacing,
  or slug. Image paths are relative to the consuming project's root; do not
  resolve them in this repository, turn them into repository URLs, or replace
  them with generated/remote URLs. Preserve other integration fields unless
  the task requires a change. If the schema cannot be satisfied while
  preserving protected values, stop and explain the conflict before editing.
- Do not run server start/stop, deploy, restore, publish, or release operations
  without explicit authorization. In particular, the local stop helper can
  match unrelated Paper processes and restore replaces archived server items.
