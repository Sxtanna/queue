package org.ipvp.queue.list;

public abstract class QueueList
{

	public static final long EXPIRATION_TIME = 900000;


	/**
	 * Removes all expired entries
	 */
	public abstract void cleanup();

	/**
	 * Remembers the position of a player for a set amount of time.
	 *
	 * @param playerName The name of the player to add.
	 * @param position   The queue position to remember.
	 */
	public abstract void rememberPosition(String playerName, int position);

	/**
	 * Forgets a player's saved position.
	 *
	 * @param playerName The name of the player to forget.
	 * @return true if player was forgotten, false if not found.
	 */
	public abstract boolean forgetPlayer(String playerName);

	/**
	 * Gets the remembered position of a player, INT_MAX if not saved.
	 *
	 * @param playerName The name of the player to check.
	 * @return The remembered position in the queue.
	 */
	public abstract int getRememberedPosition(String playerName);

}
