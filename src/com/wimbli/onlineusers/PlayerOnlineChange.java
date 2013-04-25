package com.wimbli.onlineusers;

/*
 * Utility class to store a pair of values
 */

public class PlayerOnlineChange
{
	private final String name;
	private final boolean online;

	public PlayerOnlineChange(String name, boolean online)
	{
		this.name = name;
		this.online = online;
	}

	public String getName()
	{
		return name;
	}

	public boolean getOnline()
	{
		return online;
	}
}
