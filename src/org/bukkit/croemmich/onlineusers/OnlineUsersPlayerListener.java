
package org.bukkit.croemmich.onlineusers;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class OnlineUsersPlayerListener extends PlayerListener {
    //private final OnlineUsers plugin;
    private final OnlineUsersDataSource ds;

    public OnlineUsersPlayerListener(OnlineUsers instance, OnlineUsersDataSource source) {
        //plugin = instance;
        ds = source;
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
    	ds.addUser(event.getPlayer().getName());
    }

    @Override
    public void onPlayerQuit(PlayerEvent event) {
    	if (OnlineUsers.removeOfflineUsers)
			ds.removeUser(event.getPlayer().getName());
		else {
			ds.setUserOffline(event.getPlayer().getName());
		}
    }
}
