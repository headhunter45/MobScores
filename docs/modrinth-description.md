**Mob Scores**

Mob Scores adds configurable entity-kill scoring to the required ScoreKeeper
plugin. It targets Paper 26.2 and 26.3 and requires ScoreKeeper 0.2.2 or newer.
Bukkit and Spigot are not supported.

## Requirements

- Paper 26.2 or 26.3, running Java 25 or newer.
- ScoreKeeper 0.2.2 or newer, installed and enabled.

Install both plugin JARs in the server's `plugins` directory. Mob Scores
declares ScoreKeeper as a hard dependency, so Paper will not enable it without
ScoreKeeper. If the installed version is older than 0.2.2, Mob Scores logs a
version warning and disables itself.

## Configuration

Mob Scores creates `plugins/MobScores/config.yml` with a default `ScoreTable`.
Configure integer point values by Paper API `EntityType` name:

```yaml
ScoreTable:
  ZOMBIE: 25
  CREEPER: 50
  SILVERFISH: 0
```

Edit the configuration while the server is stopped, then restart it. Unknown
entity names and malformed values are logged and skipped. Zero-valued entries
do not increase a player's numeric score and are hidden from the join-time
score table.

## Scoring

For configured entity types, Mob Scores records the latest direct player
damager or player-fired projectile shooter. That player receives the configured
points when the entity dies if still online. Environmental damage does not
clear attribution; an offline player is not awarded points and a warning is
logged. Points are added to ScoreKeeper's `mob-scores` bucket.

Mob Scores has no commands or permissions of its own. Use ScoreKeeper's
`/score-bucket mob-scores` and `/score-get` commands to view the score. Players
see the nonzero configured entity values when they join.

## Build

Build from the repository root with `./gradlew spotlessCheck build`. See
[CONTRIBUTING.md](../CONTRIBUTING.md) for contributor setup and testing status.
