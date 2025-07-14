# Modernize MobScores Plugin: Implementation Tasks

- [x] Analyze all Java source files for deprecated or removed Bukkit API usage, including logger, event registration, listener classes, configuration API, and entity/player mapping.
- [x] Identify and document any hardcoded Bukkit references or legacy patterns (e.g., Player as HashMap key, old event registration, CraftBukkit class names).
- [ ] Review and list any custom scripts or resources (e.g., bash scripts in tools/bash/) that may need migration or updates.
- [ ] Initialize Gradle in the project root and create a build.gradle file with project metadata, PaperMC API dependency, JUnit, Java toolchain, repository, resource handling, and plugins as needed.
- [ ] Remove Maven-specific files (pom.xml, .mvn/ directory, Maven wrapper scripts) and Eclipse-specific files (.classpath, .project, .settings/) if present. _(Depends on: Gradle init)_
- [ ] Update .gitignore to add Gradle-specific ignores and remove Maven/Eclipse-specific ignores. _(Depends on: Gradle init)_
- [ ] Ensure plugin.yml is present in src/main/resources and update for PaperMC compatibility (api-version, commands, required fields). _(Depends on: Gradle init)_
- [ ] Refactor all logger usage to use getLogger() from JavaPlugin.
- [ ] Refactor all event listeners to use the modern event system (Listener interface, @EventHandler, registerEvents).
- [ ] Replace use of org.bukkit.util.config.Configuration with the modern configuration API (getConfig(), saveConfig(), etc.).
- [ ] Update score table to use Bukkit entity types or enums instead of CraftBukkit class names.
- [ ] If storing player scores, refactor to use UUID as the key instead of Player or String.
- [ ] Build the plugin with Gradle (./gradlew build) and test on a modern Paper server using the provided bash scripts. _(Depends on: all refactors and plugin.yml update)_
- [ ] Address any bugs or incompatibilities found during testing on a modern server. _(Depends on: build and test)_
- [ ] Update README.md and CONTRIBUTING.md with new build, usage, and development instructions. _(Depends on: bugfixes)_
- [ ] (Optional) Add new features, quality-of-life improvements, automated tests, or CI configuration. _(Depends on: docs update)_
- [ ] Update MobScores to depend on the latest version of the ScoreKeeper plugin (update dependency in build.gradle and plugin.yml as needed).
- [ ] Test MobScores with the latest ScoreKeeper to ensure score tracking, awarding, and all integration points work as expected (including with players who have changed names).
