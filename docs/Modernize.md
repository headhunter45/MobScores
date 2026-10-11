# Mob Scores modernization status

This is the maintained modernization plan. Historical migration notes remain
in [TODO.md](../TODO.md) and [`.cursor/plans/Modernize.md`](../.cursor/plans/Modernize.md);
they are not current task status. This plan records implementation facts, not
claims that runtime scenarios have been tested.

## Implemented

- **Paper build and dependency — implemented.** The Gradle 9.8.0 wrapper,
  Java 25 toolchain, Paper API compile dependency, and compile-only ScoreKeeper
  0.2.2 dependency are configured in `build.gradle`. The plugin descriptor
  sets Paper API version 26.2 and a hard dependency on ScoreKeeper.
- **Plugin lifecycle and configuration — implemented.** The plugin loads its
  `ScoreTable` from YAML, falls back to defaults if the section is absent,
  logs and skips malformed entries, and serializes the loaded table at
  shutdown.
- **Scoring integration — implemented.** It creates the ScoreKeeper
  `mob-scores` bucket if missing, tracks the most recent eligible damager
  using entity and player UUIDs, and requests an award for an online credited
  player at entity death.
- **Join display and event handling — implemented.** Two registered Paper
  listeners handle player join, qualifying damage, and entity death. The join
  table omits zero-valued entries.
- **Build formatting and CI — implemented.** Spotless is configured; GitHub
  Actions runs `test` and builds on JDK 25.
- **Documentation entry points — implemented.** `CONTRIBUTING.md` is the
  onboarding guide, `AGENTS.md` is the shared working-rules entry point, and
  `human-tests.md` distinguishes manual scenarios from automated checks.

## Pending

- [ ] **Add server-free automated behavior tests.** Keep the normal Gradle
  `test` task independent of a running Minecraft server. Cover default and
  configured table loading, invalid and unknown entries, direct and projectile
  player attribution, latest-attacker behavior, untracked entities, offline
  players, zero values, and join messages. Completion criteria: tests exercise
  each listed behavior, fail when the behavior is deliberately broken, and
  pass under `./gradlew test` on JDK 25.
- [ ] **Decide whether opt-in real-server integration testing is worthwhile.**
  If adopted, add a separate task that is not part of normal `test` or `build`.
  Cover loading with ScoreKeeper, score awards and join messaging, and
  configuration/score persistence across restart. Completion criteria: document
  exact prerequisites, opt-in invocation, cleanup/safety behavior, and verify
  the task against Paper 26.2/26.3 with ScoreKeeper 0.2.2 or newer. Do not add
  an integration harness until there is a concrete need and safe repeatable
  setup.
- [ ] **Record manual release verification when performed.** Use
  [human-tests.md](human-tests.md) on a disposable server. Completion criteria:
  record the tested Paper, Java, Mob Scores, and ScoreKeeper versions and the
  result of each scenario. The checklist itself is not evidence of a pass.

## Validation status

No runtime validation is implied by the implementation list. The automated
test task currently has no Java test sources (`NO-SOURCE`); the pending tests
above are not implemented. See [CONTRIBUTING.md](../CONTRIBUTING.md) for
commands and [human-tests.md](human-tests.md) for manual expected behavior.
