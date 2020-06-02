package com.sxtanna.mc.queue.hook;

import net.md_5.bungee.api.config.ServerInfo;

public final class RedisBungeeHook
{

	private final com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI api;

	public RedisBungeeHook()
	{
		this.api = com.imaginarycode.minecraft.redisbungee.RedisBungee.getApi();
	}


	public boolean usable()
	{
		return this.api != null;
	}

	public int getPlayerCount(final ServerInfo server)
	{
		return api == null ? -1 : server.getPlayers().size();
	}

}
