package com.sxtanna.mc.queue.prev.trash.queue.list;

import java.util.AbstractMap.SimpleEntry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class TimedMemoryList extends QueueMemoryList
{

	private final ConcurrentHashMap<String, Integer> players         = new ConcurrentHashMap<>();
	private final Vector<SimpleEntry<Long, String>>  expirationTimes = new Vector<>();


	@Override
	public void cleanup()
	{
		while (!expirationTimes.isEmpty())
		{
			if (expirationTimes.get(0).getKey() < System.currentTimeMillis())
			{
				players.remove(expirationTimes.get(0).getValue());
				expirationTimes.remove(0);
			}
			else
			{
				return;
			}
		}
	}

	@Override
	public void rememberPosition(String playerName, int position)
	{
		playerName = playerName.toLowerCase();

		// If the player is already registered find its old timestamp and delete it
		forgetPlayer(playerName);

		expirationTimes.add(new SimpleEntry<>(System.currentTimeMillis() + QueueMemoryList.EXPIRATION_TIME, playerName));
		players.put(playerName, position);
	}

	@Override
	public boolean forgetPlayer(String playerName)
	{
		playerName = playerName.toLowerCase();
		if (players.containsKey(playerName))
		{
			for (SimpleEntry<Long, String> timestamp : expirationTimes)
			{
				if (timestamp.getValue().equals(playerName))
				{
					expirationTimes.remove(timestamp);
					break;
				}
			}
			players.remove(playerName);
			return true;
		}
		return false;
	}

	@Override
	public int getRememberedPosition(String playerName)
	{
		// Remove expired players first
		playerName = playerName.toLowerCase();
		cleanup();
		return players.getOrDefault(playerName, Integer.MAX_VALUE);
	}

}
