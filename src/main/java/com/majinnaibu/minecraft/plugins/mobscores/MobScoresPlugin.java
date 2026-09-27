/*
This file is part of MobScores.

MobScores is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MobScores is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with MobScores.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.majinnaibu.minecraft.plugins.mobscores;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.minecraft.plugins.mobscores.listeners.MobDeathListener;
import com.majinnaibu.minecraft.plugins.mobscores.listeners.PlayerConnectListener;
import com.majinnaibu.minecraft.plugins.scorekeeper.ScoreKeeperPlugin;

public class MobScoresPlugin extends JavaPlugin {
	private static final String SCORE_BUCKET_ID = "mob-scores";
	private static final String MIN_SCORE_KEEPER_VERSION = "0.2.2";

	private Map<UUID, UUID> _claimedMobs = new HashMap<UUID, UUID>();
	private Map<EntityType, Integer> _scoreTable = new HashMap<EntityType, Integer>();
	private ScoreKeeperPlugin _scoreKeeper = null;

	private final String _logPrefix = "[MobScores] ";
	private final Component _messagePrefix = Component.text("[")
		.append(Component.text("MobScores").color(NamedTextColor.AQUA))
		.append(Component.text("] ").color(NamedTextColor.WHITE));

	@Override
	public void onDisable() {
		getConfig().set("ScoreTable", serializeScoreTable(_scoreTable));
		saveConfig();
	}

	@Override
	public void onEnable() {
		// Create the default config if it doesn't exist.
		saveDefaultConfig();

		// Load our score table from config or set defaults.
		if (getConfig().isConfigurationSection("ScoreTable")) {
			_scoreTable = deserializeScoreTable(getConfig().getConfigurationSection("ScoreTable").getValues(false));
		} else {
			_scoreTable = getDefaultScoreTable();
			getConfig().set("ScoreTable", serializeScoreTable(_scoreTable));
			saveConfig();
		}

		PluginManager pm = getServer().getPluginManager();
		ScoreKeeperPlugin scoreKeeper = (ScoreKeeperPlugin)pm.getPlugin("ScoreKeeper");
		
		if (scoreKeeper == null) {
			logWarning("Unable to find ScoreKeeper plugin.");
			pm.disablePlugin(this);
			return;
		}

		String scoreKeeperVersion = scoreKeeper.getPluginMeta().getVersion();
		if (!isVersionAtLeast(scoreKeeperVersion, MIN_SCORE_KEEPER_VERSION)) {
			logWarning(
					"ScoreKeeper "
							+ MIN_SCORE_KEEPER_VERSION
							+ " or newer is required; found "
							+ scoreKeeperVersion
							+ ".");
			pm.disablePlugin(this);
			return;
		}
		_scoreKeeper = scoreKeeper;
		if (_scoreKeeper.getBucket(SCORE_BUCKET_ID) == null) {
			_scoreKeeper.createBucket(SCORE_BUCKET_ID, "mob point", "mob points", 0);
		}
		
		pm.registerEvents(new MobDeathListener(this), this);
		pm.registerEvents(new PlayerConnectListener(this), this);

		logInfo(getPluginMeta().getName() + " version " + getPluginMeta().getVersion() + " is enabled!");
	}

	private HashMap<EntityType, Integer> getDefaultScoreTable() {
		HashMap<EntityType, Integer> scores = new HashMap<EntityType, Integer>();
		
		scores.put(EntityType.BLAZE, 100);
		scores.put(EntityType.CAVE_SPIDER, 100);
		scores.put(EntityType.CREEPER, 50);
		scores.put(EntityType.DROWNED, 25);
		scores.put(EntityType.ELDER_GUARDIAN, 250);
		scores.put(EntityType.ENDERMAN, 100);
		scores.put(EntityType.ENDERMITE, 1000);
		scores.put(EntityType.END_CRYSTAL, 250);
		scores.put(EntityType.GHAST, 250);
		scores.put(EntityType.GIANT, 250);
		scores.put(EntityType.GUARDIAN, 250);
		scores.put(EntityType.HUSK, 25);
		scores.put(EntityType.PILLAGER, 25);
		scores.put(EntityType.RAVAGER, 50);
		scores.put(EntityType.SHULKER, 50);
		scores.put(EntityType.SILVERFISH, 0);
		scores.put(EntityType.SKELETON, 50);
		scores.put(EntityType.WITHER_SKELETON, 100);
		scores.put(EntityType.ZOMBIE, 25);
		scores.put(EntityType.SLIME, 100);
		scores.put(EntityType.SPIDER, 50);
		
		return scores;
	}

	public void claimMob(Entity entity, Player damager) {
		if (_scoreTable.containsKey(entity.getType())) {
			_claimedMobs.put(entity.getUniqueId(), damager.getUniqueId());
		}	
	}

	public void awardScore(Entity entity) {
		UUID playerId = _claimedMobs.remove(entity.getUniqueId());
		if(playerId != null){
			EntityType type = entity.getType();
			if (_scoreTable.containsKey(type)) {
				Player player = getServer().getPlayer(playerId);
				if (player == null) {
					logWarning("Unable to award score because the credited player is offline.");
					return;
				}
				int score = _scoreTable.get(type);
				_scoreKeeper.addScore(player, SCORE_BUCKET_ID, score);
			} else {
				logWarning("Unable to award score for {" + type.toString() + "}");
			}
		}	
	}

	public void sendPlayerScoreTable(Player player) {
		for (Map.Entry<EntityType, Integer> entry : _scoreTable.entrySet()) {
			if (entry.getValue() != 0) {
				EntityType type = entry.getKey();
				Component message = Component.translatable(type.translationKey())
								.append(Component.text(" = "))
								.append(Component.text(String.valueOf(entry.getValue()))
								.color(NamedTextColor.GREEN));
				sendPlayerMessage(player, message);
			}
		}
	}

	private Map<EntityType, Integer> deserializeScoreTable(Map<String, Object> rawValue) {
		HashMap<EntityType, Integer> scoreTable = new HashMap<>();
		if (rawValue instanceof Map) {
			scoreTable = new HashMap<>();
			for (Map.Entry<String, Object> entry : rawValue.entrySet()) {
				try {
					EntityType type = EntityType.valueOf(entry.getKey());
					Integer value = ((Number) entry.getValue()).intValue();
					scoreTable.put(type, value);
				} catch (Exception ex) {
					logError(ex);
				}
			}
			return scoreTable;
		} else {
			return getDefaultScoreTable();
		}
	}

	private Map<String, Integer> serializeScoreTable(Map<EntityType, Integer> scoreTable) {
        Map<String, Integer> serializable = new HashMap<>();
        for (Map.Entry<EntityType, Integer> entry : scoreTable.entrySet()) {
            serializable.put(entry.getKey().name(), entry.getValue());
        }
        return serializable;
    }

	public void sendPlayerMessage(Player player, Component message) {
		player.sendMessage(_messagePrefix.append(message));
	}

	public void sendPlayerMessage(Player player, String message) {
		player.sendMessage(_messagePrefix.append(Component.text(message)));
	}

	public void logError(Exception ex) {
		getLogger().log(Level.SEVERE, _logPrefix + ex.toString());
	}

	public void logInfo(String messag) {
		getLogger().info(_logPrefix + messag);
	}

	public void logWarning(String message) {
		getLogger().warning(_logPrefix + message);
	}

	private boolean isVersionAtLeast(String version, String minimumVersion) {
		int[] actual = parseVersion(version);
		int[] minimum = parseVersion(minimumVersion);
		if (actual == null || minimum == null) {
			return false;
		}
		for (int index = 0; index < actual.length; index++) {
			if (actual[index] != minimum[index]) {
				return actual[index] > minimum[index];
			}
		}
		return !isPrerelease(version);
	}

	private int[] parseVersion(String version) {
		if (version == null) {
			return null;
		}
		String coreVersion = version.replaceFirst("^[vV]", "");
		int metadataIndex = coreVersion.indexOf('+');
		if (metadataIndex >= 0) {
			coreVersion = coreVersion.substring(0, metadataIndex);
		}
		int prereleaseIndex = coreVersion.indexOf('-');
		if (prereleaseIndex >= 0) {
			coreVersion = coreVersion.substring(0, prereleaseIndex);
		}
		String[] parts = coreVersion.split("\\.");
		if (parts.length < 3) {
			return null;
		}
		try {
			return new int[] {
					Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])
			};
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private boolean isPrerelease(String version) {
		String release = version.replaceFirst("^[vV]", "");
		int metadataIndex = release.indexOf('+');
		if (metadataIndex >= 0) {
			release = release.substring(0, metadataIndex);
		}
		return release.indexOf('-') >= 0;
	}
}
