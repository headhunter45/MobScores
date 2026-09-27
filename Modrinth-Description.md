**MobScores**

MobScores awards ScoreKeeper points for configured mob kills. Set a score for each supported entity type and let ScoreKeeper track the player's total by UUID.

## Requirements

- Paper 1.21.7 or a compatible newer server.
- Java 25 or newer at runtime and for building.
- ScoreKeeper 0.2.2 or newer.

Install ScoreKeeper and MobScores in the server's `plugins` directory. ScoreKeeper is a required dependency and must be enabled for MobScores to load.

## Configuration

MobScores creates `plugins/MobScores/config.yml` from its defaults. Edit `ScoreTable` to configure point values by Bukkit `EntityType` name:

```yaml
ScoreTable:
  ZOMBIE: 25
  CREEPER: 50
  SILVERFISH: 0
```

Set an entity's value to `0` to disable its points and hide it from the join-time score table. Restart the server after changing the configuration.

## Scoring

MobScores records credit for configured entity types damaged directly by a player or by a projectile fired by a player. When a credited player remains online when the entity dies, its configured points are added to the dedicated `mob-scores` ScoreKeeper bucket. Use `/score-bucket mob-scores` to view mob points with ScoreKeeper commands. Other mob attribution is not currently handled.

MobScores has no commands of its own. Players see the nonzero score table when they join.

## Build

Run `./gradlew test` and `./gradlew build` from the project root. See [CONTRIBUTING.md](CONTRIBUTING.md) for development and local Paper testing instructions.
