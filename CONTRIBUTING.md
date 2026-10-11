# Contributing

This is the single onboarding guide for people contributing to Mob Scores. Read
the shared [AGENTS.md](AGENTS.md) before changing project files; it records the
cross-project rules for contributors and coding agents.

## First contribution

1. Read the user-facing behavior in [README.md](README.md), then check
   [AGENTS.md](AGENTS.md) for project conventions and safety constraints.
2. Find the relevant code using the [source map](#source-map-and-architecture)
   below. Keep behavior changes within the requested scope and update the
   affected documentation.
3. Build and run the available checks as described in
   [Build and automated checks](#build-and-automated-checks). If changing
   gameplay behavior, use the separate manual scenarios in
   [docs/human-tests.md](docs/human-tests.md); they are not automated tests.
4. Review `git diff` and `git status` before handing off. Do not include
   generated output, local server data, or `.env` files.

## Source map and architecture

- [MobScoresPlugin.java](src/main/java/com/majinnaibu/minecraft/plugins/mobscores/MobScoresPlugin.java)
  owns plugin startup and shutdown, loads and saves the `ScoreTable`, checks
  ScoreKeeper's presence and minimum version, creates the `mob-scores` bucket
  if needed, tracks the latest eligible damager by entity/player UUID, and
  calls ScoreKeeper to award points.
- [MobDeathListener.java](src/main/java/com/majinnaibu/minecraft/plugins/mobscores/listeners/MobDeathListener.java)
  records direct-player and player-fired-projectile damage and requests an
  award when an entity dies.
- [PlayerConnectListener.java](src/main/java/com/majinnaibu/minecraft/plugins/mobscores/listeners/PlayerConnectListener.java)
  sends the nonzero configured score-table entries when a player joins.
- [config.yml](src/main/resources/config.yml) supplies the default entity
  scores. [plugin.yml](src/main/resources/plugin.yml) declares the main class,
  Paper API version, and hard ScoreKeeper dependency.
- [build.gradle](build.gradle) configures the Java/Gradle build, Paper and
  ScoreKeeper compile-time dependencies, resource version expansion, formatting,
  and Maven publication.

Mob Scores keeps the configuration table; player score storage and commands
belong to the required ScoreKeeper plugin. Mob Scores registers two event
listeners and declares no commands or permissions of its own. Its current
credit policy is the most recent qualifying direct or projectile player
damager; environmental damage does not clear that attribution. An award is
attempted only if that player is online when the entity dies.
Mob Scores does not register a Bukkit service or declare a separate public
integration API; it uses ScoreKeeper's plugin API internally.

## Prerequisites

- Java 25 for the build toolchain and Paper 26.2/26.3 runtime.
- Gradle 9.8.0, supplied by the checked-in wrapper (`./gradlew`).
- For in-game testing only: a Paper 26.2 or 26.3 server and ScoreKeeper 0.2.2
  or newer. The build itself does not require a running server.
- For `tools/build-plugin.sh`: `curl`, `tar`, and a supported macOS/Linux
  architecture; the helper downloads Temurin JDK 25 under the project Gradle
  user home if it cannot find a JDK there.

The Gradle configuration enables Java toolchain auto-download and uses the
Foojay resolver. Alternatively, set `JAVA_HOME` to a JDK 25 installation.

## Build and automated checks

From the repository root:

```sh
./gradlew spotlessCheck build
./gradlew test
```

`./gradlew build` runs the normal Gradle build and checks, including
`spotlessCheck`. The plugin JAR is written to `build/libs/`. The wrapper is
Gradle 9.8.0; CI runs on Ubuntu with JDK 25, runs `test`, then builds with
`tools/build-plugin.sh`.

There are currently no Java test sources and no `integrationTest` task.
Consequently, `./gradlew test` reports `NO-SOURCE`; JUnit is declared as a
dependency but does not currently provide behavior coverage. A successful
build verifies compilation and configured checks, not runtime behavior or
ScoreKeeper compatibility on a server.

For a build helper that selects/downloads Temurin 25 and then runs the Gradle
build, use:

```sh
./tools/build-plugin.sh
```

It accepts Gradle task arguments, for example
`./tools/build-plugin.sh spotlessCheck`.

## Local Paper testing and helper scripts

Manual runtime testing is optional for ordinary documentation or build changes.
For gameplay changes, use a disposable server and the checklist in
[docs/human-tests.md](docs/human-tests.md). Install ScoreKeeper 0.2.2 or newer
alongside Mob Scores. See [docs/modrinth-description.md](docs/modrinth-description.md)
for the concise public project description.

The start, deploy, backup, restore, and Modrinth publishing helpers read `.env`
at the repository root as `KEY=value` data; copy `.env.example` to `.env` and
configure paths locally. The parser does not execute shell expressions, and
`MINECRAFT_ENV_FILE` is not supported. The standalone stop helper reads
`STOP_TIMEOUT` from its process environment, and the build helper does not use
`.env`. Keep `.env`, tokens, and server/player data out of commits.

- `tools/deploy-plugin.sh` copies the newest non-`-all` JAR in `build/libs/`
  into `$MINECRAFT_SERVER_PATH/plugins/`; it does not restart the server.
- `tools/start-server.sh` can download a Paper server JAR and launch it, but
  first invokes `tools/stop-server.sh`. That stop script matches any local Java
  process whose command line includes a `paper-*.jar` and can force-kill it
  after its timeout. Inspect processes and server paths before using either
  script; do not use them on a machine with unrelated Paper servers.
- `tools/backup-server.sh` requires a stopped server by default.
  `tools/restore-server.sh` replaces top-level server items from an archive;
  inspect the archive and target paths before any restore.
- `tools/publish-local.sh` publishes to the local Maven cache.
  `tools/publish-modrinth.sh` uploads a release and requires a Modrinth token.
  Do not run release, publishing, deployment, restore, or server lifecycle
  operations without explicit authorization.

## Optional reference material

These are references, not alternate onboarding guides:

- [README.md](README.md) — supported behavior and installation.
- [docs/human-tests.md](docs/human-tests.md) — manual pre-release scenarios
  and expected behavior; not a report of tests already passed.
- [docs/Modernize.md](docs/Modernize.md) — maintained modernization status and
  pending work.
- [docs/project-description.md](docs/project-description.md),
  [docs/project-description.json](docs/project-description.json), and
  [docs/modrinth.json](docs/modrinth.json) — local project-description sources.
