# MobScores

MobScores awards ScoreKeeper points for configured mob kills. It is a Paper plugin and requires the ScoreKeeper plugin to be installed and enabled first.

## Requirements

- Paper 1.21.7 or a compatible newer server.
- Java 25 to build the plugin.
- ScoreKeeper 0.2.2 or the version declared by `build.gradle`.

Install both plugin JARs in the server's `plugins` directory. MobScores declares ScoreKeeper as a hard dependency, so it will not enable without it.

## Score Table

On first startup, MobScores creates `plugins/MobScores/config.yml` with default scores for supported entity types. Edit the `ScoreTable` mapping to change points; keys are Bukkit `EntityType` names and values are integer points:

```yaml
ScoreTable:
  ZOMBIE: 25
  CREEPER: 50
  SILVERFISH: 0
```

Restart the server after editing the configuration. A configured value of `0` suppresses that entity from the score table shown to players when they join.

MobScores has no commands of its own. Current killer attribution is recorded for zombie entities directly damaged by a player; projectile and other mob attribution are not currently handled. Points are awarded through ScoreKeeper's existing player API and follow the player's UUID-backed score.

## Build

Run `./gradlew test` and `./gradlew build` from the project root. See [CONTRIBUTING.md](CONTRIBUTING.md) for toolchain, local server, and helper-script details.

