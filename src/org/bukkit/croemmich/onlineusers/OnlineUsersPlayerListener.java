
package org.bukkit.croemmich.onlineusers;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class OnlineUsersPlayerListener extends PlayerListener {
    //private final OnlineUsers plugin;

    public OnlineUsersPlayerListener(OnlineUsers instance) {
       //plugin = instance;
    }
    
    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	OnlineUsers.ds.addUser(event.getPlayer().getName());
    }

    @Override
    public void onPlayerQuit(PlayerEvent event) {
    	if (OnlineUsers.removeOfflineUsers)
    		OnlineUsers.ds.removeUser(event.getPlayer().getName());
		else {
			OnlineUsers.ds.setUserOffline(event.getPlayer().getName());
		}
    }
}
