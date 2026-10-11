# Mob Scores

[![Java CI with Gradle](https://github.com/headhunter45/MobScores/actions/workflows/gradle.yml/badge.svg)](https://github.com/headhunter45/MobScores/actions/workflows/gradle.yml)

Mob Scores adds configurable entity-kill scoring to the required ScoreKeeper
plugin. It targets Paper 26.2 and 26.3 only; Bukkit and Spigot are not
supported.

## Requirements and installation

- Paper 26.2 or 26.3 with Java 25 or newer.
- ScoreKeeper 0.2.2 or newer, installed and enabled before Mob Scores.

Place both plugin JARs in the server's `plugins` directory. Mob Scores declares
ScoreKeeper as a hard dependency, so Paper will not enable Mob Scores without
it. If the installed ScoreKeeper version is older than 0.2.2, Mob Scores logs a
version warning and disables itself.

## Configuration

On first startup, Mob Scores creates `plugins/MobScores/config.yml` with a
default `ScoreTable`. Configure integer point values using Paper API
`EntityType` names:

```yaml
ScoreTable:
  ZOMBIE: 25
  CREEPER: 50
  SILVERFISH: 0
```

Edit the file while the server is stopped, then restart it. The table is loaded
at startup and saved to YAML when the plugin shuts down. Unknown entity names
or malformed values are logged and skipped. A missing score-table section
falls back to the default table. Zero-valued entries do not increase a
player's score and are omitted from the join-time score table.

## Scoring behavior

For an entity type present in `ScoreTable`, Mob Scores records a direct player
damager or the player shooter of a projectile. The latest qualifying player
damager receives credit when the entity dies, provided that player is online
then. Environmental damage does not clear recorded credit; an entity with no
recorded player attribution awards no points. If the credited player is offline
at death, no award is made and a warning is logged.

Points are added to ScoreKeeper's `mob-scores` bucket, which Mob Scores creates
if it does not already exist. Mob Scores does not switch the player's active
bucket. Use ScoreKeeper's `/score-bucket mob-scores` and `/score-get` commands
to view the score. On join, players receive the configured nonzero entity
values. Mob Scores declares no commands or permissions of its own.

## Build

Run `./gradlew spotlessCheck build` from the project root. The plugin JAR is
written to `build/libs/`. See [CONTRIBUTING.md](CONTRIBUTING.md) for the
first-contribution path, build/test details, and safety notes for local-server
helpers.
