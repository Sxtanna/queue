/*
package com.sxtanna.mc.queue.prev.redis;

import com.google.common.primitives.Longs;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.sxtanna.mc.queue.prev.QueuedList;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class RedisQueuedList implements QueuedList
{

	private static final String REDIS_QUEUED_BASE = "queue:players:";


	private final String rootPath;


	public RedisQueuedList(final String serverName)
	{
		this.rootPath = REDIS_QUEUED_BASE + serverName;
	}


	@Override
	public int size()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			return jedis.scard(rootPath).intValue();
		}
	}

	@Override
	public long getPosition(final UUID uuid)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			return attemptParseLong(jedis.get(rootPath + ":" + uuid));
		}
	}

	@Override
	public long setPosition(final UUID uuid, final long position)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			jedis.sadd(rootPath, uuid.toString());
			final long previous = attemptParseLong(jedis.getSet(rootPath + ":" + uuid, String.valueOf(position)));

			jedis.pexpire(rootPath, QueuedList.EXPIRES_AFTER);
			jedis.pexpire(rootPath + ":" + uuid, QueuedList.EXPIRES_AFTER);

			return previous;
		}
	}

	@Override
	public boolean vacant()
	{
		return size() == 0;
	}

	@Override
	public boolean delete(final UUID uuid)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			final boolean removed = jedis.del(rootPath + ":" + uuid) > 0;

			if (removed)
			{
				jedis.srem(rootPath, uuid.toString());
			}

			return removed;
		}
	}

	@Override
	public Optional<UUID> next()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			final Set<String> keys = jedis.keys(rootPath + ":*");


			String uuid = null;
			Long   next = null;

			for (final String key : keys)
			{
				final long temp = attemptParseLong(jedis.get(key));

				if (next == null || temp < next)
				{
					uuid = key;
					next = temp;
				}
			}

			if (uuid != null)
			{
				return Optional.of(UUID.fromString(uuid.substring(uuid.lastIndexOf(':') + 1)));
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<UUID> last()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			final Set<String> keys = jedis.keys(rootPath + ":*");


			String uuid = null;
			Long   next = null;

			for (final String key : keys)
			{
				final long temp = attemptParseLong(jedis.get(key));

				if (next == null || temp > next)
				{
					uuid = key;
					next = temp;
				}
			}

			if (uuid != null)
			{
				return Optional.of(UUID.fromString(uuid));
			}
		}

		return Optional.empty();
	}


	@Override
	public void vacate()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			jedis.del(rootPath);

			final String[] keys = jedis.keys(rootPath + ":*").toArray(new String[0]);
			if (keys.length > 0)
			{
				jedis.del(keys);
			}
		}
	}


	private static long attemptParseLong(final String value)
	{
		if (value == null)
		{
			return QueuedList.IS_NOT_QUEUED;
		}

		//noinspection UnstableApiUsage
		final Long parsed = Longs.tryParse(value);
		if (parsed == null)
		{
			return QueuedList.IS_NOT_QUEUED;
		}

		return parsed;
	}

}
*/
