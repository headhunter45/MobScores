# Modernize MobScores Minecraft Plugin

This plan outlines the steps to modernize the MobScores plugin, migrating from an old Maven/Bukkit setup to a modern Gradle/PaperMC build, and updating project structure and configuration for best practices.

---

## 1. Analyze Current Codebase
- [ ] Review all Java source files for deprecated or removed Bukkit API usage.
- [ ] Identify any hardcoded Bukkit references or legacy patterns (e.g., using Player as HashMap key, old event registration, etc.).
- [ ] Note any custom scripts or resources that may need migration.

## 2. Migrate Build System: Maven → Gradle
- [ ] Initialize Gradle in the project root (if not already present).
- [ ] Create a `build.gradle` file with:
    - Project metadata (group, version, description) from `pom.xml`.
    - PaperMC API dependency (latest stable, e.g., 1.19+).
    - JUnit (or modern test framework) for tests.
    - Java toolchain/source compatibility (Java 17+ recommended for Paper 1.19+).
    - PaperMC repository for dependencies.
    - Resource handling for `plugin.yml`.
    - Shadow plugin or equivalent for fat-jar if needed.
- [ ] Remove Maven-specific files:
    - `pom.xml`
    - `.mvn/` directory (if present)
    - Any Maven wrapper scripts
- [ ] Remove Eclipse-specific files (e.g., `.classpath`, `.project`, `.settings/`) if present.

## 3. Update .gitignore
- [ ] Add Gradle-specific ignores:
    - `.gradle/`
    - `build/`
    - `!gradle/wrapper/`
    - `*.iml`, `.idea/` (if using IntelliJ)
- [ ] Remove Maven-specific ignores (e.g., `target/`).
- [ ] Remove Eclipse-specific ignores (e.g., `.classpath`, `.project`, `.settings/`).

## 4. Update plugin.yml
- [ ] Ensure `plugin.yml` is present in `src/main/resources`.
- [ ] Update API version and commands for PaperMC compatibility.
- [ ] Add/verify all required fields (name, main, version, api-version, commands, etc.).

## 5. Refactor Deprecated API Usage
- [ ] Replace all usages of `Player` as a HashMap key with `UUID`.
- [ ] Update logger usage to `getLogger()` from `JavaPlugin`.
- [ ] Update event listeners to use `@EventHandler` and `registerEvents`.
- [ ] Update player lookup logic to use `getPlayerExact` or handle case sensitivity.
- [ ] Review and update any other deprecated or removed Bukkit/Spigot API calls.

## 6. Test on Modern Paper Server
- [ ] Build the plugin with Gradle (`./gradlew build`).
- [ ] Use the provided bash scripts in `tools/bash/` to start/stop a local Paper server for testing.
- [ ] Set `MINECRAFT_SERVER_PATH` and `MINECRAFT_SERVER_JAR` environment variables as needed.
- [ ] Deploy the built plugin and check for errors/warnings.

## 7. Fix Bugs and Incompatibilities
- [ ] Address any issues found during testing on a modern server.
- [ ] Ensure all features work as intended.

## 8. Update Documentation
- [ ] Update `README.md` with new build and usage instructions.
- [ ] Document any new features, changes, or migration notes.
- [ ] Update or create `CONTRIBUTING.md` with setup and development instructions.

## 9. Optional Improvements
- [ ] Consider adding new features or quality-of-life improvements.
- [ ] Add automated tests or CI configuration if desired.

---

### Summary Table
| Step                        | Description                                    |
|-----------------------------|------------------------------------------------|
| Analyze Codebase            | Review for deprecated/legacy code              |
| Migrate to Gradle           | Init Gradle, migrate metadata/deps, remove Maven|
| Update .gitignore           | Add Gradle ignores, remove Maven/Eclipse        |
| Update plugin.yml           | Ensure modern, complete config                  |
| Refactor Deprecated APIs    | Update code for modern PaperMC                  |
| Test on Paper               | Build, deploy, and test on modern server        |
| Fix Bugs                    | Address issues found during testing             |
| Update Docs                 | Refresh README, CONTRIBUTING, etc.              |
| Optional Improvements       | Add features, tests, CI, etc.                   |

---

## Appendix: Gradle vs. Maven

### Maven
**Pros:**
- Stability & Maturity: Very stable, predictable builds.
- Convention over Configuration: Standard structure and lifecycle.
- Dependency Management: Handles dependencies well, large central repository.
- Documentation: Extensive documentation and community support.
- IDE Support: Excellent integration with Java IDEs.
- Widely Used: Many Minecraft plugins and tutorials use Maven.

**Cons:**
- Verbose Configuration: `pom.xml` can become large and hard to read.
- Less Flexible: Custom build logic is harder to implement.
- Slower for Large Projects: Can be slower than Gradle for complex builds.

### Gradle
**Pros:**
- Performance: Generally faster builds, supports incremental builds and caching.
- Flexibility: Build scripts in Groovy/Kotlin allow complex logic.
- Concise Configuration: `build.gradle` files are usually shorter and easier to read.
- Modern Tooling: Better support for modern build features.
- Growing Popularity: Increasingly popular in the Java ecosystem.

**Cons:**
- Learning Curve: More complex for beginners, especially for custom logic.
- Less Convention: More freedom can lead to less consistency.
- Slightly Less Documentation: Not as much Minecraft-specific documentation as Maven.

### Which is Most Common for Minecraft Plugins?
- **Maven** is still the most common for Bukkit, Spigot, and Paper plugins, with most guides and examples using it.
- **Gradle** is gaining popularity, especially for newer projects or those needing more flexibility and speed.

**Summary:**
- For maximum compatibility with community resources, Maven is the safest choice.
- For a modern, flexible, and fast build system, Gradle is a great option if you're comfortable with it. 

---

## Appendix: Choosing a Plugin API

### Bukkit
**Description:**
The original plugin API for Minecraft servers, now largely unmaintained. Most modern APIs are built on or forked from Bukkit.

**Pros:**
- Huge legacy plugin library.
- Simple, well-documented API.
- Good for very basic plugins.

**Cons:**
- No longer actively maintained.
- Lacks support for modern Minecraft features.
- Not recommended for new projects.

### Spigot
**Description:**
A high-performance fork of Bukkit, Spigot is the most widely used server software for plugins. It adds performance improvements and bug fixes.

**Pros:**
- Actively maintained and widely used.
- Large plugin ecosystem.
- Good documentation and community support.
- Compatible with most Bukkit plugins.

**Cons:**
- Lags behind the latest Minecraft features compared to Paper.
- Fewer advanced features than Paper.

### Paper
**Description:**
A fork of Spigot with additional performance optimizations, bug fixes, and new API features. Paper is now the de facto standard for modern plugin development.

**Pros:**
- Actively maintained and very popular.
- Superior performance and stability.
- Adds many new API features not in Spigot/Bukkit.
- Backwards compatible with most Spigot/Bukkit plugins.
- Large, active community.

**Cons:**
- Some Paper-specific APIs may not work on Spigot (if you ever want to support both).
- Slightly faster update cycle may require more frequent plugin updates.

### Purpur
**Description:**
A fork of Paper with even more features, configuration options, and experimental changes. Aimed at server owners who want maximum customization.

**Pros:**
- All benefits of Paper, plus more features and config options.
- Great for highly customized servers.

**Cons:**
- Some features are experimental and may be unstable.
- Smaller community than Paper/Spigot.
- Plugins using Purpur-specific features may not work elsewhere.

### Sponge
**Description:**
A completely separate API and server implementation, not based on Bukkit/Spigot/Paper. Aimed at modded servers (Forge) but also works standalone.

**Pros:**
- Designed for both plugins and mods (Forge integration).
- Modern, flexible API.
- Good for modded servers.

**Cons:**
- Much smaller plugin ecosystem for vanilla servers.
- Not compatible with Bukkit/Spigot/Paper plugins.
- Less relevant for standard server-only plugins.

### Summary & Recommendation
- **Paper** is the best choice for most modern server-only plugins: it’s fast, stable, actively maintained, and has the richest API.
- **Spigot** is a safe fallback if you want maximum compatibility, but Paper is almost always preferred now.
- **Purpur** is great for highly customized servers, but not necessary unless you want its extra features.
- **Sponge** is only recommended if you want to support modded servers or need its unique API.

**For your use case (server-only, modern, not needing experimental features):**
**Paper** is the best option. It gives you the most features, best performance, and widest compatibility for new plugin development. 

---

## Appendix: Development Setup

### Minecraft Launchers (Java Edition)

**Official Minecraft Launcher**
- Fully supported by Mojang/Microsoft.
- Works on Windows, macOS, and Linux (including WSL with GUI support).
- Easiest for vanilla play and account management.

**Prism Launcher** (formerly PolyMC)
- Open source, cross-platform (Windows, macOS, Linux).
- Great for managing multiple Minecraft instances, modpacks, and versions.
- Works well in Linux environments.

**MultiMC**
- Similar to Prism Launcher, but older and less actively maintained.
- Good for managing multiple instances.

**Recommendation:**
- Use the Official Minecraft Launcher for playing and account management.
- Use Prism Launcher for advanced instance/modpack management, especially on Linux/macOS.

### Server Software for Plugin Development

**Paper**
- Most popular for plugin development.
- Fast, stable, and actively maintained.
- Easy to run in headless/automated environments (Linux containers, CI).
- [Download Paper](https://papermc.io/downloads)

**Spigot**
- Still widely used, but less feature-rich than Paper.
- Requires building with BuildTools ([spigotmc.org/wiki/buildtools](https://www.spigotmc.org/wiki/buildtools/))

**Purpur**
- Fork of Paper with more options.
- [Download Purpur](https://purpurmc.org/downloads)

**Recommendation:**
- Use Paper for your main development and testing server.
- Download the latest Paper jar and run it directly in your Linux environment or containers.

### Automated Testing & Linux Containers
- Paper runs perfectly in headless Linux environments (including WSL, Docker, CI/CD).
- You can script server startup, plugin deployment, and automated plugin tests using bash scripts.
- For CI/CD, use GitHub Actions, GitLab CI, or any Linux-based runner.

### Summary Table

| Use Case                | Recommended Launcher         | Recommended Server |
|-------------------------|-----------------------------|-------------------|
| Playing Minecraft       | Official Launcher / Prism   | N/A               |
| Plugin Development      | N/A                         | Paper             |
| Automated Testing/CI    | N/A                         | Paper             |
| Multi-instance/modpacks | Prism Launcher              | Paper             |

### Next Steps
- Download the Official Minecraft Launcher for playing.
- Download Prism Launcher if you want advanced management.
- Download the latest Paper server jar for plugin development and testing. 

#### Server Management Scripts and Environment Variables
- Use the `tools/bash/start-server.sh` and `tools/bash/stop-server.sh` scripts to start and stop your Paper server for development and testing.
- Set the following environment variables in your shell:
  - `MINECRAFT_SERVER_PATH`: The root path of your Paper server directory.
  - `MINECRAFT_SERVER_JAR`: The path to your Paper server jar (relative to `MINECRAFT_SERVER_PATH` or absolute).
- See `CONTRIBUTING.md` for detailed setup instructions. 

---

## Appendix: Migrating from Maven to Gradle

Switching from Maven to Gradle involves several steps to ensure a smooth transition and a working build system for your plugin.

### Migration Steps
- **Initialize Gradle in the Project**
  - Use `gradle init` or manually create a `build.gradle` file.
- **Migrate Project Metadata**
  - Set `group`, `version`, and `description` in `build.gradle`.
- **Migrate Dependencies**
  - Copy dependencies from `pom.xml` to the `dependencies` block in `build.gradle`.
  - Add the PaperMC (or Spigot) repository.
- **Configure Java Version**
  - Set the Java toolchain or source/target compatibility in `build.gradle`.
- **Migrate Resource Handling**
  - Ensure `plugin.yml` and any other resources are in `src/main/resources`.
- **Migrate Build Plugins/Tasks**
  - If you used Maven plugins (e.g., for shading), add equivalent Gradle plugins (e.g., Shadow plugin).
- **Update .gitignore**
  - Add Gradle-specific files (`.gradle/`, `build/`) and remove Maven-specific ones (`target/`).
- **Remove Maven Files**
  - Remove `pom.xml` and any Maven wrapper files if not needed.
- **Update Documentation and Scripts**
  - Update README, CONTRIBUTING, and any scripts to use Gradle commands (`./gradlew build`, etc.).
- **Test the Build**
  - Run `./gradlew build` and verify the output jar works as expected.

### Task List for Migration
- [ ] Initialize Gradle build system
- [ ] Set project metadata in `build.gradle`
- [ ] Add repositories and dependencies
- [ ] Configure Java version
- [ ] Ensure resource handling for `plugin.yml`
- [ ] Add Gradle plugins as needed (e.g., Shadow)
- [ ] Update `.gitignore` for Gradle
- [ ] Remove Maven files
- [ ] Update documentation and scripts
- [ ] Test the Gradle build

#### Summary Table
| Step                        | Description                                    |
|-----------------------------|------------------------------------------------|
| Initialize Gradle           | Create `build.gradle` or use `gradle init`     |
| Project Metadata            | Set group, version, description                |
| Dependencies                | Add PaperMC repo and dependencies              |
| Java Version                | Set Java toolchain/source compatibility        |
| Resource Handling           | Ensure `plugin.yml` in resources               |
| Build Plugins               | Add Shadow or other plugins as needed          |
| .gitignore                  | Add Gradle files, remove Maven files           |
| Remove Maven Files          | Delete `pom.xml`, Maven wrapper                |
| Documentation/Scripts       | Update to use Gradle                           |
| Test Build                  | Run and verify `./gradlew build`               |

**Do not make any code or config changes until this plan is complete and reviewed.** 

## Appendix: Codebase Analysis and Migration Notes

### Deprecated or Removed API Usage
- **Logger usage:**
  - Uses `Logger.getLogger("Minecraft")`. Should use `getLogger()` from `JavaPlugin` for modern plugins.
- **Old event registration:**
  - Uses deprecated Bukkit event registration (`pm.registerEvent(...)` with `Event.Type` and `Event.Priority`). Should use the modern event system (`@EventHandler` and `registerEvents`).
- **Listener classes:**
  - Uses `EntityListener` and `PlayerListener` base classes, which are deprecated. Should use `Listener` interface and annotate handler methods with `@EventHandler`.
- **Configuration API:**
  - Uses `org.bukkit.util.config.Configuration`, which is removed in modern Bukkit/Spigot/Paper. Should use `JavaPlugin#getConfig()` and the modern configuration API.
- **Entity/Player mapping:**
  - Uses `HashMap<Entity, Player>` for claimed mobs. This is generally fine, but if player scores are stored, use `UUID` as the key for reliability across sessions.
- **Direct CraftBukkit class names:**
  - Score table uses class names like `org.bukkit.craftbukkit.entity.CraftZombie`. Should use Bukkit API types or entity type enums for compatibility.

### Hardcoded Bukkit References and Legacy Patterns
- Imports are from `org.bukkit.*`, which is compatible with Paper. No hardcoded Bukkit-only features found, but all event and config usage must be updated for Paper.
- No use of Bukkit’s old event system in active code, but event registration and listener patterns are outdated.

### Custom Scripts and Resources
- **Bash scripts in `tools/bash/`:**
  - `deploy-plugin.sh`: Deploys the built plugin jar to the server's plugins directory, using `MINECRAFT_SERVER_PATH`.
  - `build-plugin.sh`: Runs `./gradlew build` from the project root.
- These scripts are compatible with a Gradle-based workflow and do not require migration, but should be updated if the build or deployment process changes.

### Recommendations for Paper Migration
- Switch all logger usage to `getLogger()` from `JavaPlugin`.
- Refactor all event listeners to use the modern event system (`Listener` interface, `@EventHandler`, and `registerEvents`).
- Replace use of `org.bukkit.util.config.Configuration` with the modern configuration API (`getConfig()`, `saveConfig()`, etc.).
- Update score table to use Bukkit entity types or enums instead of CraftBukkit class names.
- If storing player scores, use `UUID` as the key.
- Review and update all event and configuration usage for compatibility with modern PaperMC.
- Ensure all commands are properly defined in `plugin.yml`.

#### Summary Table
| Issue/Pattern                | Status / Recommendation                |
|------------------------------|----------------------------------------|
| Logger                       | ❌ Use getLogger()                     |
| Old event registration       | ❌ Use @EventHandler, Listener, registerEvents |
| Listener base classes        | ❌ Use Listener interface, @EventHandler|
| Configuration API            | ❌ Use getConfig(), saveConfig()        |
| Entity/Player mapping        | ⚠️ Use UUID for player scores if needed|
| CraftBukkit class names      | ⚠️ Use Bukkit API/entity types          |
| Command registration         | ✅ Valid, check plugin.yml              |
| Hardcoded Bukkit features    | ✅ None found, but update all event/config usage |
| Custom scripts               | ✅ Compatible, update if build/deploy changes | 