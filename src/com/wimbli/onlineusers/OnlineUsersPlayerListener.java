
package com.wimbli.onlineusers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class OnlineUsersPlayerListener implements Listener {
    
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	OnlineUsers.ds.addUser(event.getPlayer().getName());
    }

	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	if (OnlineUsers.removeOfflineUsers)
    		OnlineUsers.ds.removeUser(event.getPlayer().getName());
		else {
			OnlineUsers.ds.setUserOffline(event.getPlayer().getName());
		}
    }
}
