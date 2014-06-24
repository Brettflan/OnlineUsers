package com.wimbli.onlineusers;
import java.util.UUID;

/*
 * Utility class to store a set of values
 */

public class PlayerOnlineChange
{
	private final String name;
	private final UUID uuid;
	private final boolean online;

	public PlayerOnlineChange(String name, UUID uuid, boolean online)
	{
		this.name = name;
		this.uuid = uuid;
		this.online = online;
	}

	public String getName()
	{
		return name;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public boolean getOnline()
	{
		return online;
	}
}
