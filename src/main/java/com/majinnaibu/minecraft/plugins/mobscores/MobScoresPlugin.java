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
import java.util.Set;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.majinnaibu.minecraft.plugins.mobscores.listeners.MobDeathListener;
import com.majinnaibu.minecraft.plugins.mobscores.listeners.PlayerConnectListener;
import com.majinnaibu.minecraft.plugins.scorekeeper.ScoreKeeperPlugin;

public class MobScoresPlugin extends JavaPlugin {
	private Map<Entity, Player> _claimedMobs = new HashMap<Entity, Player>();
	private Map<String, Integer> _scoreTable = new HashMap<String, Integer>();
	private ScoreKeeperPlugin _scoreKeeper = null;

	private final String _logPrefix = "[MobScores] ";
	private final Component _messagePrefix = Component.text("[")
		.append(Component.text("MobScores").color(NamedTextColor.AQUA))
		.append(Component.text("] ").color(NamedTextColor.WHITE));

	@Override
	public void onDisable() {
		getConfig().set("ScoreTable", _scoreTable);
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		// Create the default config if it doesn't exist.
		saveDefaultConfig();

		// Load our score table from config or set defaults.
		if (getConfig().contains("ScoreTable")) {
			Object rawValue = getConfig().get("ScoreTable");
			if (rawValue instanceof Map) {
				_scoreTable = new HashMap<>((Map<String, Integer>) rawValue);
			} else {
				_scoreTable = getDefaultScoreTable();
			}
		} else {
			_scoreTable = getDefaultScoreTable();
			getConfig().set("ScoreTable", _scoreTable);
			saveConfig();
		}

		PluginManager pm = getServer().getPluginManager();
		_scoreKeeper = (ScoreKeeperPlugin)pm.getPlugin("ScoreKeeper");
		
		if(_scoreKeeper == null){
			pm.disablePlugin(this);
		}
		
		pm.registerEvents(new MobDeathListener(this), this);
		pm.registerEvents(new PlayerConnectListener(this), this);

		logInfo(getPluginMeta().getName() + " version " + getPluginMeta().getVersion() + " is enabled!");
	}

	private HashMap<String, Integer> getDefaultScoreTable() {
		HashMap<String, Integer> scores = new HashMap<String, Integer>();
		
		scores.put("org.bukkit.craftbukkit.entity.CraftZombie", 50);
		scores.put("org.bukkit.craftbukkit.entity.CraftChicken", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftCow", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftCreeper", 50);
		scores.put("org.bukkit.craftbukkit.entity.CraftGhast", 100);
		scores.put("org.bukkit.craftbukkit.entity.CraftGiant", 250);
		scores.put("org.bukkit.craftbukkit.entity.CraftPig", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftPigZombie", 25);
		scores.put("org.bukkit.craftbukkit.entity.CraftPlayer", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftSheep", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftSkeleton", 50);
		scores.put("org.bukkit.craftbukkit.entity.CraftSlime", 100);
		scores.put("org.bukkit.craftbukkit.entity.CraftSpider", 50);
		scores.put("org.bukkit.craftbukkit.entity.CraftSquid", 0);
		scores.put("org.bukkit.craftbukkit.entity.CraftWolf", 50);
		scores.put("org.bukkit.craftbukkit.entity.CraftZombie", 50);
		
		return scores;
	}

	public void claimMob(Entity entity, Player damager) {
		if(entity instanceof Zombie){
			_claimedMobs.put((Zombie)entity, damager);
		}
		
	}

	public void awardScore(Entity entity) {

		if(_claimedMobs.containsKey(entity)){
			Class<?> scoreClass = entity.getClass();
			String className = scoreClass.getName();
			if(_scoreTable.containsKey(className)){
				Player player = _claimedMobs.get(entity);
				int score = _scoreTable.get(className);
				
				_scoreKeeper.addScore(player, score);
			}else{
				logWarning("Unable to award score for {" + className + "}");
				Set<String> keys = _scoreTable.keySet();
				String str = null;
				for(Iterator<String> i = keys.iterator(); i.hasNext(); str = i.next()){
					logInfo("{" + str + "}");
				}
			}
		}	
	}

	public void sendPlayerScoreTable(Player player) {
		Iterator<Map.Entry<String, Integer>> i=_scoreTable.entrySet().iterator();
		Map.Entry<String, Integer> pair = null;
		for(pair = i.next(); i.hasNext(); pair = i.next()){
			if(pair.getValue() != 0){
				String key = pair.getKey();
				if(key.startsWith("org.bukkit.craftbukkit.entity.Craft")){
					sendPlayerMessage(player, key.substring(35) + " = " + pair.getValue().toString());
				}else{
					sendPlayerMessage(player, key + " = " + pair.getValue().toString());
				}
			}
		}
	}

	public void sendPlayerMessage(Player player, String message) {
		player.sendMessage(_messagePrefix.append(Component.text(message)));
	}

	public void logInfo(String messag) {
		getLogger().info(_logPrefix + messag);
	}

	public void logWarning(String message) {
		getLogger().warning(_logPrefix + message);
	}
}
