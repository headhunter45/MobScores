# Mob Scores manual pre-release checklist

This is a list of manual scenarios, not a record of tests already performed.
Use a disposable Paper 26.2 or 26.3 server running Java 25 or newer, with
Mob Scores and ScoreKeeper 0.2.2 or newer. Keep the server log visible.

## Test setup

- Back up the test world and plugin data folders. Never test against data that
  cannot be safely replaced.
- Use two ordinary player accounts and an operator for setup.
- Let Mob Scores create `plugins/MobScores/config.yml` on first startup.
  Configure the `ScoreTable` while the server is stopped; the plugin reads the
  table on startup and saves it to YAML at shutdown.
- Record player UUIDs, entity types, configured scores, active ScoreKeeper
  buckets, and numeric scores before and after each scenario.

## Manual checklist

### Loading and configuration

- With both plugins installed, verify ScoreKeeper loads and Mob Scores enables.
  Verify the `mob-scores` bucket exists with singular label `mob point`,
  plural label `mob points`, and initial score `0`.
- On a disposable server, start without ScoreKeeper and then with a version
  older than 0.2.2. The hard dependency should prevent loading when missing;
  with an older installed version, Mob Scores should log a version warning
  and disable itself.
- On first startup, compare the generated `ScoreTable` with the defaults in
  `src/main/resources/config.yml`, including the zero-valued `SILVERFISH`.
- Change valid values and restart. Confirm the values are loaded and written
  back on shutdown. Include an unknown entity key and a malformed value:
  verify each is logged and skipped while valid entries still load. After
  shutdown, confirm only the loaded valid entries remain in the saved table.
- Remove the `ScoreTable` section on a disposable test and restart. Verify the
  default table is used and written to the configuration.

### Attribution and scoring

- Kill configured entities using direct player damage and player-fired
  projectiles. Confirm the latest qualifying player's numeric score increases
  by the configured amount in `mob-scores`.
- Have two players damage the same tracked entity. Confirm the player with the
  most recent direct or player-projectile hit receives credit.
- After a player damages a tracked entity, let it die from fall, fire, or
  another environmental cause. Confirm the last recorded player still receives
  credit if online at death; environmental damage does not clear attribution.
- Kill an entity with no prior qualifying player damage. Confirm no score
  increase. Repeat with a type absent from `ScoreTable` and with non-player
  damage that has no previous player attribution.
- Configure an entity type with value `0`. Confirm it does not increase the
  numeric score and is omitted from the join-time table.
- Disconnect the credited player before the entity dies. Confirm no numeric
  score is awarded and the log reports that the credited player is offline.
- In multiplayer combat and after chunk unload/reload, confirm attribution
  does not move to another player and a death does not reuse a previous
  entity's attribution.

### Join messages and ScoreKeeper workflows

- Join with a player account. Confirm each nonzero configured entity type and
  value is sent, and zero-valued entries are omitted. Message ordering is not
  guaranteed.
- Run `/score-bucket mob-scores` followed by `/score-get`. Confirm it reports
  the awarded numeric score. Switch to another ScoreKeeper bucket and back;
  Mob Scores should only add points to `mob-scores` and should not switch the
  active bucket on join or kill.
- Restart the server, reconnect, and verify the YAML score table and
  ScoreKeeper-managed score totals are available as before.

## Automated coverage and checks

Mob Scores currently has no Java test sources and no `integrationTest` task.
Gradle declares a JUnit dependency, but `./gradlew test` has no tests to run
(`NO-SOURCE`). CI runs the Gradle test task and build; neither proves event
attribution or live Paper/ScoreKeeper compatibility. This checklist is for
manual pre-release verification only; record actual results separately.
