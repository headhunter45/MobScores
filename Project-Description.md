# MobScores

MobScores turns combat into a configurable scoring system. When a player damages a tracked mob, the plugin remembers the attribution and awards the matching ScoreKeeper bucket when that mob dies, including projectile kills.

It is a deliberately small Paper integration plugin with a clear boundary: Paper entity events on one side, ScoreKeeper's public scoring API on the other. The per-entity score table is configurable, zero-value entries can be disabled, and players see the active mob-score table when they join. It targets Paper 26.2 and 26.3 only, not Bukkit or Spigot.

## Engineering Notes

- Built for modern Paper with Java 25 and Gradle.
- Uses UUID-safe player attribution.
- Depends on ScoreKeeper 0.2.2 or newer.
- Tested through the standard server-free build workflow.
