package org.ipvp.queue;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

public class TimedList
{
	private ConcurrentHashMap<String, Integer> players = new ConcurrentHashMap<>();
	private Vector<SimpleEntry<Long, String>> expirationTimes = new Vector<>();
	private static long EXPIRATION_TIME = 900000;

	public void cleanup()
	{

		while(!expirationTimes.isEmpty())
		{
			if(expirationTimes.get(0).getKey() > System.currentTimeMillis())
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

	/**
	 * Remembers the position of a player for a set amount of time.
	 * @param playerName The name of the player to add.
	 * @param position The queue position to remember.
	 */
	public void rememberPosition(String playerName, int position)
	{
		// Remove expired players first
		cleanup();

		// If the player is already registered find its old timestamp and delete it
		forgetPlayer(playerName);

		expirationTimes.add(new SimpleEntry<>(System.currentTimeMillis() + EXPIRATION_TIME, playerName));
		players.put(playerName, position);
	}

	/**
	 * Forgets a player's saved position.
	 * @param playerName The name of the player to forget.
	 * @return true if player was forgotten, false if not found.
	 */
	public boolean forgetPlayer(String playerName)
	{
		if(players.containsKey(playerName))
		{
			for (SimpleEntry<Long, String> timestamp : expirationTimes)
			{
				if(timestamp.getValue().equals(playerName))
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

	/**
	 * Gets the remembered position of a player, INT_MAX if not saved.
	 * @param playerName The name of the player to check.
	 * @return The remembered position in the queue.
	 */
	public int getRememberedPosition(String playerName)
	{
		// Remove expired players first
		cleanup();
		return players.getOrDefault(playerName, Integer.MAX_VALUE);
	}
}
