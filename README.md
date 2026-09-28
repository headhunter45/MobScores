# MobScores

[![Java CI with Gradle](https://github.com/headhunter45/MobScores/actions/workflows/gradle.yml/badge.svg)](https://github.com/headhunter45/MobScores/actions/workflows/gradle.yml)

MobScores awards ScoreKeeper points for configured mob kills. It is a Paper-only plugin for Paper 26.2 and 26.3, and requires the ScoreKeeper plugin to be installed and enabled first.

## Requirements

- Paper 26.2 or 26.3.
- Java 25 to build the plugin.
- ScoreKeeper 0.2.2 or newer.

Install both plugin JARs in the server's `plugins` directory. MobScores declares ScoreKeeper as a hard dependency, so it will not enable without it. Bukkit and Spigot servers are not supported.

## Score Table

On first startup, MobScores creates `plugins/MobScores/config.yml` with default scores for supported entity types. Edit the `ScoreTable` mapping to change points; keys are Paper API `EntityType` names and values are integer points:

```yaml
ScoreTable:
  ZOMBIE: 25
  CREEPER: 50
  SILVERFISH: 0
```

Restart the server after editing the configuration. A configured value of `0` suppresses that entity from the score table shown to players when they join. Entity type keys use the Paper API's `EntityType` names.

MobScores has no commands of its own. It records credit for configured entity types damaged directly by a player or by a projectile fired by a player. Other mob attribution is not currently handled. Points go into ScoreKeeper's dedicated `mob-scores` bucket and are tracked by player UUID. Use `/score-bucket mob-scores` before `/score-get` to view mob points; MobScores does not change the player's active bucket automatically.

## Build

Run `./gradlew test` and `./gradlew build` from the project root. See [CONTRIBUTING.md](CONTRIBUTING.md) for toolchain, local server, and helper-script details.
