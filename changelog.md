# Changelog for v0.2.3

<!--
  Release notes for the next release of MobScores. Write Markdown below this comment.
  Everything under the header (HTML comments excluded) becomes the GitHub release and
  Modrinth changelog; the release version itself comes from the tag.
  After a release, reset this file for the next version and tag that commit
  v<next>-dev (see CONTRIBUTING.md).
-->

## Changed

- Scores now go into a dedicated `mob-scores` bucket in ScoreKeeper, which Mob Scores creates on first start if it does not exist. This works with the buckets added in ScoreKeeper 0.2.2.
- **Requires ScoreKeeper 0.2.2 or newer.** If an older ScoreKeeper is installed, Mob Scores logs a warning and disables itself.
- Now targets Paper 26.2 (`api-version: '26.2'`) and runs on Paper 26.2 and 26.3 with Java 25. Paper 1.21.x is no longer supported.
- Fixed nullability warnings in the plugin and listeners.

## Development

- Builds against ScoreKeeper from the Modrinth Maven repository instead of a local snapshot.
- Source is formatted with Spotless (google-java-format 1.36.1) and carries a license header.
- Added GitHub Actions for CI (build, test, upload jar) and draft GitHub releases from `v*` tags. Versions come from `v*` Git tags and release notes come from this file.
- Rewrote the README (requirements, installation, configuration) and reorganized the documentation.
