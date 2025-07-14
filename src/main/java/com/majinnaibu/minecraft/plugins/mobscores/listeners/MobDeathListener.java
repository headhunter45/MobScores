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

package com.majinnaibu.minecraft.plugins.mobscores.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.majinnaibu.minecraft.plugins.mobscores.MobScoresPlugin;

public class MobDeathListener implements Listener {
	private MobScoresPlugin _plugin = null; 
	
	public MobDeathListener(MobScoresPlugin plugin) {
		_plugin = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
			Entity damager = ev.getDamager();
			if(damager instanceof Player){
				_plugin.claimMob(ev.getEntity(), (Player)damager);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		_plugin.awardScore(event.getEntity());
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
	}

}
