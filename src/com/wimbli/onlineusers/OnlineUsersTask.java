package com.wimbli.onlineusers;

import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.scheduler.BukkitRunnable;


public class OnlineUsersTask extends BukkitRunnable
{
	private static LinkedBlockingQueue<PlayerOnlineChange> playerChanges = new LinkedBlockingQueue<PlayerOnlineChange>();

	public static void addPlayerChange(PlayerOnlineChange change)
	{
		playerChanges.add(change);
	}

	public void run()
	{
		while(!playerChanges.isEmpty())
		{
			PlayerOnlineChange change = playerChanges.poll();
			if (change == null)
				return;

			if (change.getOnline())
		    	OnlineUsers.ds.addUser(change.getName());
			else if (OnlineUsers.removeOfflineUsers)
				OnlineUsers.ds.removeUser(change.getName());
			else
				OnlineUsers.ds.setUserOffline(change.getName());
		}
	}
}
