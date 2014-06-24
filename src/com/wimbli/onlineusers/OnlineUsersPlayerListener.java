
package com.wimbli.onlineusers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class OnlineUsersPlayerListener implements Listener {
    
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	OnlineUsersTask.addPlayerChange(new PlayerOnlineChange(event.getPlayer().getName(), event.getPlayer().getUniqueId(), true));
    }

	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	OnlineUsersTask.addPlayerChange(new PlayerOnlineChange(event.getPlayer().getName(), event.getPlayer().getUniqueId(), false));
    }
}
