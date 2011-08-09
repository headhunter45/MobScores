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

package com.majinnaibu.bukkit.plugins.mobscores;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.majinnaibu.bukkit.plugins.mobscores.listeners.MobDeathListener;
import com.majinnaibu.bukkit.plugins.mobscores.listeners.PlayerConnectListener;
import com.majinnaibu.bukkit.plugins.scorekeeper.ScoreKeeperPlugin;

public class MobScoresPlugin extends JavaPlugin {
	private final MobDeathListener _mobDeathListener = new MobDeathListener(this);
	private final PlayerConnectListener _playerConnectListener = new PlayerConnectListener(this);
	private HashMap<Entity, Player> _claimedMobs = new HashMap<Entity, Player>();
	private HashMap<String, Integer> _scoreTable = new HashMap<String, Integer>();
	private ScoreKeeperPlugin _scoreKeeper = null;
	private Configuration _configuration = null;

	private final String _logStart = "[" + ChatColor.AQUA + "MobScores" + ChatColor.WHITE + "] ";
	
	public final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		_configuration.setProperty("ScoreTable", _scoreTable);
		_configuration.save();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		_configuration = getConfiguration();
		try{
			_scoreTable = (HashMap<String, Integer>)_configuration.getProperty("ScoreTable");
		}catch (Exception ex){
			_scoreTable = getDefaultScoreTable();
		}
		
		if(_scoreTable == null){
			_scoreTable = getDefaultScoreTable();
		}
		
		_configuration.setProperty("ScoreTable", _scoreTable);
		
		
		_configuration.save();
		
		PluginManager pm = getServer().getPluginManager();
		_scoreKeeper = (ScoreKeeperPlugin)pm.getPlugin("ScoreKeeper");
		
		if(_scoreKeeper == null){
			pm.disablePlugin(this);
		}
		
		pm.registerEvent(Event.Type.ENTITY_DEATH, _mobDeathListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, _mobDeathListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, _playerConnectListener, Event.Priority.Monitor, this);

		PluginDescriptionFile pdFile = this.getDescription();
		log.info(pdFile.getName() + " version " + pdFile.getVersion() + " is enabled!");
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
		// TODO Auto-generated method stub
		
		if(_claimedMobs.containsKey(entity)){
			Class<?> scoreClass = entity.getClass();
			String className = scoreClass.getName();
			if(_scoreTable.containsKey(className)){
				Player player = _claimedMobs.get(entity);
				int score = _scoreTable.get(className);
				
				_scoreKeeper.addScore(player, score);
			}else{
				log.warning("[MobScores] Unable to award score for {" + className + "}");
				Set<String> keys = _scoreTable.keySet();
				String str = null;
				for(Iterator<String> i = keys.iterator(); i.hasNext(); str = i.next()){
					log.info("{" + str + "}");
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
					player.sendMessage(_logStart + key.substring(35) + " = " + pair.getValue().toString());
				}else{
					player.sendMessage(_logStart +  key + " = " + pair.getValue().toString());
				}
			}
		}
	}

	public void logInfo(String string) {
		log.info(_logStart + string);
	}
}
