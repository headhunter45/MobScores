/*
This file is part of Mob Scores.

Mob Scores is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Mob Scores is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Mob Scores. If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>.
*/

package com.majinnaibu.minecraft.plugins.mobscores.listeners;

import com.majinnaibu.minecraft.plugins.mobscores.MobScoresPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectListener implements Listener {
  private MobScoresPlugin _plugin = null;

  public PlayerConnectListener(MobScoresPlugin plugin) {
    _plugin = plugin;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    _plugin.sendPlayerScoreTable(event.getPlayer());
  }
}
