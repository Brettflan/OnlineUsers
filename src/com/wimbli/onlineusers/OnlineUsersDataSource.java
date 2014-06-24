package com.wimbli.onlineusers;
import java.util.UUID;

public abstract class OnlineUsersDataSource {
	
	public OnlineUsersDataSource() {
		
	}
	
	public abstract boolean init();
	
	public abstract boolean addUser(String username, UUID uuid);
	
	public abstract boolean removeUser(String username, UUID uuid);
	
	public abstract boolean setUserOffline(String username, UUID uuid);
	
	public abstract boolean setAllOffline();
	
	public abstract boolean removeAllUsers();
	
}