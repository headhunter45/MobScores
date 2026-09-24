# MobScores — Project Reference

> A quick orientation guide for anyone on the team picking up this project.
> Written for engineers, designers, and PMs who need to understand *what this
> plugin is, how it works, and what state it's in* without re-deriving it from
> the source. Last refreshed from a full read of the code on 2026-09-23.

## 1. What it is

MobScores is a **Minecraft server plugin** that awards a player **points whenever
they kill a monster (mob)**. The list of mobs and the points each one is worth
are **configurable**. MobScores does not keep the scores itself — it hands the
points to a companion plugin we call **ScoreKeeper**.

- Type: Bukkit/Spigot-style Java plugin (a JAR dropped into a Minecraft server's
  `plugins/` folder).
- Package: `com.majinnaibu.bukkitplugins.mobscores`
- License: **AGPL v3** (see `License-Header.txt` and `Readme.txt`).
- Copyright: (c) 2011 Tom Hicks ("headhunter45").
- Author: majinnaibu / headhunter45 / Tom Hicks.

One-line user value: **give players a reason to fight monsters — every kill
scores, and the scores roll up in ScoreKeeper.**

## 2. How it works (the scoring flow)

Three pieces: the main plugin class, two event listeners, and the ScoreKeeper
dependency.

1. **Player damages a mob** → `MobDeathListener.onEntityDamage` fires.
   - If the damage came from a `Player` (`EntityDamageByEntityEvent`), that
     player is recorded as the *claimant* of that specific mob instance
     (`MobScoresPlugin.claimMob`). Only the **last player to hit it** is the
     claimant — so a kill that ends a fight is credited to the final hitter.
   - Note the current `claimMob` only *stores* a claim when the mob is a
     `Zombie` (see §5 — this looks like a leftover/guard bug, not intended
     behaviour).
2. **The mob dies** → `MobDeathListener.onEntityDeath` fires →
   `MobScoresPlugin.awardScore(entity)`.
   - Looks the killed entity up in the "claimed mobs" map to find which player
     claimed it.
   - Looks that mob's full class name up in the **score table** to find its
     point value.
   - Calls `ScoreKeeper.addScore(player, score)` to add the points to that
     player's running total.
   - If the mob's class isn't in the score table, nothing is awarded and the
     server log lists every class that *is* scored (helpful for debugging
     config).
3. **A player joins the server** → `PlayerConnectListener.onPlayerJoin` →
   `MobScoresPlugin.sendPlayerScoreTable(player)`.
   - Messages the **current score table** (only mobs worth > 0 points) to the
     player on join, so newcomers can see what's worth killing.

Events are registered at `Monitor` priority and as listeners (the old
`EntityListener` / `PlayerListener` style, not the modern `@EventHandler`
annotation style).

## 3. Configuration — the configurable list of mobs & points

There are **no slash commands** in this plugin (confirmed in `Readme.txt` and the
absent `commands:` block in `plugin.yml`). All configuration is done by editing
the config file directly.

- **File:** `plugins/MobScores/config.yml` (the Bukkit config for this plugin).
- **What it holds:** a `ScoreTable` mapping a **mob's fully-qualified class
  name** to an integer point value, e.g.

  ```
  org.bukkit.craftbukkit.entity.CraftZombie   -> 50
  org.bukkit.craftbukkit.entity.CraftCreeper  -> 50
  org.bukkit.craftbukkit.entity.CraftGhast    -> 100
  org.bukkit.craftbukkit.entity.CraftGiant    -> 250
  ```

- **Lifecycle:** On `onEnable`, the table is loaded from config; if missing or
  unreadable, a **default table** is used (see §4). On `onDisable` the table is
  written back to config, so admin edits in `config.yml` survive restarts.
- **Default table** (defined in `getDefaultScoreTable()`):

  | Mob | Class key | Points |
  |---|---|---|
  | Zombie / Zombie (entry appears twice) | `CraftZombie` | 50 |
  | Creeper | `CraftCreeper` | 50 |
  | Skeleton | `CraftSkeleton` | 50 |
  | Spider | `CraftSpider` | 50 |
  | Wolf | `CraftWolf` | 50 |
  | Pig Zombie | `CraftPigZombie` | 25 |
  | Ghast | `CraftGhast` | 100 |
  | Slime | `CraftSlime` | 100 |
  | Giant | `CraftGiant` | 250 |
  | Chicken / Cow / Pig / Sheep / Squid / Player | `Craft*` | 0 |

  (Passive/animal mobs and players score 0 — i.e. killing them is worth nothing.)

**Gotcha for anyone editing config:** the key is the **exact runtime class name**
of the entity (`org.bukkit.craftbukkit.entity.CraftZombie`, etc.). The class
keys are also what `sendPlayerScoreTable` trims down to a short name (`Zombie`,
`Creeper`…) when messaging players. If a config key doesn't match a real runtime
class, that mob simply never scores.

## 4. The ScoreKeeper dependency (the important one)

MobScores **cannot run on its own** — it is not functional without ScoreKeeper.
On `onEnable` it looks up the `ScoreKeeper` plugin via the plugin manager and, if
it's absent, **immediately disables itself** (`pm.disablePlugin(this)`). So
ScoreKeeper must be installed and enabled first.

Two things the team should know:

- **MobScores calls ScoreKeeper as a Java object**, not as a command. It casts
  the loaded plugin to a `ScoreKeeperPlugin` type and calls
  `addScore(Player, int)` on it directly. This is why `plugin.yml` declares
  `depend: [ScoreKeeper]`.
- **Version / package mismatch to be aware of.** MobScores (as written here)
  imports the *old* ScoreKeeper API:
  `com.majinnaibu.bukkitplugins.scorekeeper.ScoreKeeperPlugin`. The
  **current** ScoreKeeper in this repo (see the ScoreKeeper project, kanban
  board slug `score-keeper`) has been **modernized** to a Paper/Gradle project on
  a different package:
  `com.majinnaibu.minecraft.plugins.scorekeeper` and a `Player`-based
  `addScore(Player, int)` with a different API shape. **As it stands, this
  MobScores source will not compile against the current ScoreKeeper** — the
  package and API moved. This is almost certainly the biggest thing anyone
  touching the build needs to know first. The ScoreKeeper project keeps its own
  `README.md` / `ProjectDescription.md`.

## 5. Known issues, gaps, and "smells" (hand to engineers)

These are not just trivia — they're the real backlog this project generates:

- **Compatibility:** old Bukkit API (pre-1.0, ~2011): `JavaPlugin`
  `registerEvent` / `EntityListener` / `PlayerListener` style, and
  `org.bukkit.util.config.Configuration` (that package was removed in Minecraft
  1.13). Java **source/target 1.6**. It will not build or run on a modern
  Paper/Spigot server or modern JDK without a migration. (The sibling
  ScoreKeeper was already migrated to Paper 1.21.x / Java 21 — MobScores has
  not kept up.)
- **Build config drift:** `plugin.yml` `main:` is written as
  `com.majinnaibu.bukkit.plugins.mobscores.MobScoresPlugin`
  (note `bukkit.plugins`), but the actual source package is
  `com.majinnaibu.bukkitplugins.mobscores` — the `main` class path likely needs
  fixing to load the plugin. `plugin.yml` also lists `version: 0.1` while
  `pom.xml` is `0.2-SNAPSHOT`.
- **The `Zombie` guard in `claimMob`:** a new mob is only stored as "claimed"
  when it `instanceof Zombie`. Any non-Zombie mob never gets a claimant, so
  `awardScore` finds no player and awards nothing. This looks like an
  over-eager guard/leftover rather than intended behaviour — worth confirming.
- **`awardScore` has a `// TODO Auto-generated method stub`** comment still in
  place; the method works but the stub is not cleaned up.
- **No persistence of claims / no edge cases:** claims live in memory; a
  claimed mob that is killed by the environment (fire, fall, explode —
  `onEntityExplode` is an empty stub) isn't handled. Score awarding depends on
  ScoreKeeper being up.
- **No tests, no CI** in this repo; `pom.xml` references a `bukkit` artifact
  `0.0.1-SNAPSHOT` from a Bukkit repo that may no longer be reachable, and JUnit
  3.8.1 (very old). Building will need a real Bukkit/Spigot API dependency.

## 6. Repository & build

- **Location:** this repo; remote `origin` → `git@github.com:headhunter45/MobScores.git`.
- **Layout:** a Maven project (the `MobScores/` folder holds `pom.xml`,
  `src/main/java/...`, `src/main/resources/plugin.yml`, `Manifest.MF`,
  `Readme.txt`). Also has Eclipse `.project`/`.classpath`/`.settings` and a
  `MobScores.jardesc`.
- **Branches:** `main` and `develop` (currently in sync — no diff between them).
- **Build:** Maven (`mvn package` in the `MobScores` folder), producing a JAR.
  Caveat: it won't build against the current ScoreKeeper (see §4) or a modern
  server without the migration work in §5.

## 7. File map (what's what)

| Path | Role |
|---|---|
| `MobScores/pom.xml` | Maven build (Java 1.6, bukkit dep, JUnit 3.8.1) |
| `MobScores/src/main/resources/plugin.yml` | Plugin descriptor: name, main class, `depend: [ScoreKeeper]` |
| `MobScores/src/main/java/com/.../mobscores/MobScoresPlugin.java` | Main plugin class: config load/save, score table, claim/award logic, join messaging |
| `MobScores/.../mobscores/listeners/MobDeathListener.java` | Handles `EntityDamage` (claim) + `EntityDeath` (award) + `EntityExplode` (stub) |
| `MobScores/.../mobscores/listeners/PlayerConnectListener.java` | On player join, sends them the score table |
| `MobScores/Readme.txt` | Author note: "No commands. Edit `plugins/MobScores/config.yml` to set scores per mob." |
| `MobScores/Manifest.MF`, `License-Header.txt` | JAR manifest and the AGPL license header on every file |

## 8. TL;DR for a new person

MobScores is a **small, old Bukkit plugin**: *kill a mob → the last player who
hit it gets points from a configurable table → points are handed to ScoreKeeper
→ on join the player sees the current table.* It is tightly coupled to
ScoreKeeper and currently **incompatible with the modern version of it**, and it
targets a pre-1.0 Bukkit API that most of us build against today. Expect a
"port it to the current ScoreKeeper + modern server API" task before any feature
work, plus a few cleanups flagged in §5.

<!-- Companion projects: ScoreKeeper (kanban board slug "score-keeper"),
     Metropolis (sibling in the Minecraft workspace). -->
