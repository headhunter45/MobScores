# MobScores Human Testing

Use this checklist on a disposable Paper 26.2 or 26.3 server with Java 25+, MobScores, and ScoreKeeper 0.2.2 or newer installed. MobScores depends on ScoreKeeper for its `mob-scores` bucket, and its main behavior is driven by real combat and player-join events.

## Test Setup

- Back up the world and both plugin data folders. Configure a test server with the same plugin versions intended for production.
- Use at least two ordinary player accounts and an operator for setup. Keep the server log visible to catch dependency, config, and offline-award warnings.
- Save the generated `plugins/MobScores/config.yml` before changing it. Restart after config edits; MobScores reads the score table during plugin startup.
- Record player UUIDs, mob types, configured values, active score buckets, and scores before and after each kill.

## Human Checklist

### Plugin loading and configuration

- Start with both plugins installed. Confirm ScoreKeeper loads before MobScores, MobScores enables, and the `mob-scores` bucket exists with the expected labels and initial value.
- On a disposable server, test startup without ScoreKeeper and with a ScoreKeeper version older than 0.2.2. Paper should reject the missing hard dependency; MobScores should disable itself with a version warning for an outdated dependency. Then restore the supported version.
- On first startup, inspect the generated `ScoreTable`. Confirm the documented default entity values are present, including a zero-valued entry.
- Change a few values, restart, and verify they are loaded and preserved. Include a zero value and an unknown entity key; confirm zero-valued entities are omitted from join messages and invalid entries are logged without preventing valid entries from loading.
- Confirm the table uses Paper `EntityType` names and that malformed values or a missing score-table section are handled as intended.

### Kill credit and scoring

- As a player, kill configured entities using direct melee damage and player-fired projectiles. Confirm the expected points are added to that player's `mob-scores` score.
- Check the boundary cases: a non-player attacker, environmental death, an entity type absent from the table, and a configured zero-point entity. Confirm no unintended points are awarded.
- Have two players damage the same mob before it dies. The current implementation records the most recent direct or player-projectile attacker; verify that this credit policy matches the server's expectations.
- Damage a mob with a player, then let it die from fall, fire, or another environmental cause. Confirm whether retaining that player's credit is the intended behavior; the current implementation keeps the last recorded player attribution until the mob dies.
- Test a player who disconnects after damaging the mob but before it dies. The current implementation cannot award an offline player and logs a warning; confirm this is acceptable and that no other player's score changes.
- Check mob death without any previous qualifying player damage, and verify no stale or reused entity attribution grants points.

### Player messaging and ScoreKeeper workflows

- Join with a player account. Confirm the MobScores score table is sent once, includes configured nonzero entity types with the right values, and excludes zero-valued types.
- Run `/score-bucket mob-scores` followed by `/score-get`. Confirm the score reflects kills and the bucket switch message uses the expected singular/plural label. Verify MobScores does not change the player's active bucket automatically on join or kill.
- Give two players different kill totals and switch between `mob-scores` and another ScoreKeeper bucket. Confirm each bucket retains its own value and MobScores awards only to `mob-scores`.
- Restart the server and verify MobScores retains its configured table and ScoreKeeper retains the players' mob scores. Reconnect and confirm the join table still matches the active config.
- Exercise normal gameplay around claimed mobs: unload/reload chunks, kill several entities rapidly, and test multiplayer combat. Watch for missed, duplicated, or cross-player awards.

## Automated Coverage

There are currently no Java test sources in MobScores and no `integrationTest` source set/task. Although Gradle declares JUnit, `./gradlew test` currently has no MobScores behavior tests. The testing tasks marked “Ready” in `TODO.md` have not been implemented. Build success alone does not verify real event attribution or compatibility with the installed ScoreKeeper jar.
