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

package com.majinnaibu.bukkit.plugins.mobscores.listeners;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import com.majinnaibu.bukkit.plugins.mobscores.MobScoresPlugin;

public class PlayerConnectListener extends PlayerListener {
private MobScoresPlugin _plugin = null; 
	
	public PlayerConnectListener(MobScoresPlugin plugin) {
		_plugin = plugin;
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		_plugin.sendPlayerScoreTable(event.getPlayer());
	}
}
