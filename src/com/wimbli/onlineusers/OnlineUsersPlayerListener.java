
package com.wimbli.onlineusers;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerListener;

public class OnlineUsersPlayerListener extends PlayerListener {
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
    	OnlineUsers.ds.addUser(event.getPlayer().getName());
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    	if (OnlineUsers.removeOfflineUsers)
    		OnlineUsers.ds.removeUser(event.getPlayer().getName());
		else {
			OnlineUsers.ds.setUserOffline(event.getPlayer().getName());
		}
    }
}
