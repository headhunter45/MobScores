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
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.minecraft.plugins.mobscores.listeners.MobDeathListener;
import com.majinnaibu.minecraft.plugins.mobscores.listeners.PlayerConnectListener;
import com.majinnaibu.minecraft.plugins.scorekeeper.ScoreKeeperPlugin;

public class MobScoresPlugin extends JavaPlugin {
	private Map<Entity, Player> _claimedMobs = new HashMap<Entity, Player>();
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

		getConfig().set("ScoreTable", serializeScoreTable(getDefaultScoreTable()));
		saveConfig();

		// Load our score table from config or set defaults.
		if (getConfig().isConfigurationSection("ScoreTable")) {
			deserializeScoreTable(getConfig().getConfigurationSection("ScoreTable").getValues(false));
		} else {
			_scoreTable = getDefaultScoreTable();
			getConfig().set("ScoreTable", serializeScoreTable(_scoreTable));
			saveConfig();
		}

		PluginManager pm = getServer().getPluginManager();
		_scoreKeeper = (ScoreKeeperPlugin)pm.getPlugin("ScoreKeeper");
		
		if(_scoreKeeper == null){
			logWarning("Unable to find ScoreKeeper plugin.");
			pm.disablePlugin(this);
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
		if(entity instanceof Zombie){
			_claimedMobs.put((Zombie)entity, damager);
		}	
	}

	public void awardScore(Entity entity) {

		if(_claimedMobs.containsKey(entity)){
			EntityType type = entity.getType();
			if (_scoreTable.containsKey(type)) {
				Player player = _claimedMobs.get(entity);
				int score = _scoreTable.get(type);
				_scoreKeeper.addScore(player, score);
			} else {
				logWarning("Unable to award score for {" + type.toString() + "}");
			}
		}	
	}

	public void sendPlayerScoreTable(Player player) {
		Iterator<Map.Entry<EntityType, Integer>> i = _scoreTable.entrySet().iterator();
		Map.Entry<EntityType, Integer> pair = null;
		for(pair = i.next(); i.hasNext(); pair = i.next()){
			if(pair.getValue() != 0){
				EntityType type = pair.getKey();
				Component entityName = Component.translatable(type.translationKey());
				sendPlayerMessage(player, entityName + " = " + pair.getValue().toString());
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
}
