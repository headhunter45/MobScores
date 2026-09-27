# Contributing

## Requirements

- Java 25. The Gradle toolchain can download it automatically; `tools/build-plugin.sh` also provisions Temurin 25 under the project Gradle user home.
- A recent Paper server for runtime testing.
- The ScoreKeeper plugin, which is a required runtime dependency.

## Build and Test

From the project root:

```sh
./gradlew test
./gradlew build
```

The distributable plugin JAR is written to `build/libs/`. To use the repository helper, run:

```sh
tools/build-plugin.sh
```

## Local Paper Testing

Set `PAPER_VERSION` and `MINECRAFT_SERVER_PATH` in a local `.env` file at the MobScores project root, or export them in your shell. Set `MINECRAFT_ENV_FILE` if the helper scripts should load a shared environment file instead. Build ScoreKeeper and install it into the server first, then build and deploy MobScores:

```sh
tools/build-plugin.sh
tools/deploy-plugin.sh
```

Start the server with `tools/start-server.sh`. The script downloads a stable Paper build when one is not already present. Review the server log for dependency and plugin-load errors, then test score-table configuration and mob-kill scoring in game. Back up the server before testing against a world with data you care about.

## Configuration

The default resource is `src/main/resources/config.yml`; Paper copies it to `plugins/MobScores/config.yml` on first startup. `ScoreTable` maps Bukkit `EntityType` names to integer point values. To change values on a test server, edit the generated config and restart the server.

## Helper Scripts

- `tools/build-plugin.sh` builds with Java 25.
- `tools/deploy-plugin.sh` copies the latest non-`-all` JAR into `$MINECRAFT_SERVER_PATH/plugins/`.
- `tools/start-server.sh` downloads/resolves and launches Paper using `.env` settings.
- `tools/backup-server.sh` and `tools/restore-server.sh` manage server backups.
- `tools/publish-local.sh` publishes the artifact to Maven Local for dependent plugin builds.
