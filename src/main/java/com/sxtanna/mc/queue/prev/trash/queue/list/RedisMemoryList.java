package com.sxtanna.mc.queue.prev.trash.queue.list;

import com.google.common.primitives.Ints;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;

public final class RedisMemoryList extends QueueMemoryList
{

	@Override
	public void cleanup()
	{
		// not needed, cause redis :D
	}

	@Override
	public void rememberPosition(String playerName, int position)
	{
		forgetPlayer(playerName);

		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{

			// PSETEX = Set with expiration specified in milliseconds

			jedis.psetex("queue:players:" + playerName.toLowerCase(), EXPIRATION_TIME, String.valueOf(position));
		}
	}

	@Override
	public boolean forgetPlayer(String playerName)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{

			// return of DEL = amount of keys deleted, if this is > 0, they were deleted or "forgotten"

			return jedis.del("queue:players:" + playerName.toLowerCase()) > 0;
		}
	}

	@Override
	public int getRememberedPosition(String playerName)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{

			// return of GET will be null if either not stored, or the key expired

			final String position = jedis.get("queue:players:" + playerName.toLowerCase());
			if (position == null)
			{
				return Integer.MAX_VALUE;
			}

			//noinspection UnstableApiUsage
			final Integer parsed = Ints.tryParse(position);
			if (parsed == null)
			{
				return Integer.MAX_VALUE;
			}

			return parsed;
		}
	}

}
