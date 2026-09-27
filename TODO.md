# Modernize MobScores Plugin: Implementation Tasks

|  ID  | Status | Title |
|:-----:|:-----:|:------|
| MS-01 |   Done   | Analyze all Java source files for deprecated or removed Bukkit API usage, including logger, event registration, listener classes, configuration API, and entity/player mapping. |
| MS-02 |   Done   | Identify and document any hardcoded Bukkit references or legacy patterns (e.g., Player as HashMap key, old event registration, CraftBukkit class names). |
| MS-03 |   Done   | Review and list any custom scripts or resources (e.g., bash scripts in tools/bash/) that may need migration or updates.|
| MS-04 |   Done   | Initialize Gradle in the project root and create a build.gradle file with project metadata, PaperMC API dependency, JUnit, Java toolchain, repository, resource handling, and plugins as needed. |
| MS-05 |   Done   | Remove Maven-specific files (pom.xml, .mvn/ directory, Maven wrapper scripts) and Eclipse-specific files (.classpath, .project, .settings/) if present. |
| MS-06 |   Done   | Update .gitignore to add Gradle-specific ignores and remove Maven/Eclipse-specific ignores. |
| MS-07 |   Done   |Ensure plugin.yml is present in src/main/resources and update for PaperMC compatibility (api-version, commands, required fields). |
| MS-08 |   Done   | Refactor all logger usage to use getLogger() from JavaPlugin. |
| MS-09 |   Done   | Refactor all event listeners to use the modern event system (Listener interface, @EventHandler, registerEvents).
| MS-10 |   Done   | Replace use of org.bukkit.util.config.Configuration with the modern configuration API (getConfig(), saveConfig(), etc.).
| MS-11 |   Done   | Update score table to use Bukkit entity types or enums instead of CraftBukkit class names.
| MS-12 |   Done   | If storing player scores, refactor to use UUID as the key instead of Player or String.
| MS-13 |   Done   | Build the plugin with Gradle (./gradlew build) and test on a modern Paper server using the provided bash scripts.
| MS-14 |   Done   | Address any bugs or incompatibilities found during testing on a modern server.
| MS-15 |   Done   | Update README.md and CONTRIBUTING.md with new build, usage, and development instructions. _(Depends on: bugfixes)_
| MS-16 |   Done   | (Optional) Add new features, quality-of-life improvements, automated tests, or CI configuration. _(Depends on: docs update)_
| MS-17 |   Done   | Update MobScores to depend on the latest version of the ScoreKeeper plugin (update dependency in build.gradle and plugin.yml as needed).
| MS-18 |   Done   | Test MobScores with the latest ScoreKeeper to ensure score tracking, awarding, and all integration points work as expected (including with players who have changed names).
| MS-19 |   Done   | Refactor MobDeathListener and PlayerConnectListener to implement Listener interface and use @EventHandler annotations instead of extending EntityListener/PlayerListener.
| MS-20 |   Done   | Update event registration in MobScoresPlugin to use getServer().getPluginManager().registerEvents(...).
| MS-21 |   Done   | Replace all usage of org.bukkit.util.config.Configuration with the modern Bukkit configuration API (getConfig(), saveConfig(), reloadConfig(), etc.).
| MS-22 |   Done   | Remove or refactor any code using deprecated or removed Bukkit/Spigot/Paper APIs that are not available in the modern Paper API.
