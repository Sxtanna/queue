package com.sxtanna.mc.queue.impl.impl;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.sxtanna.mc.queue.impl.base.Users;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class RedisUsers implements Users
{

	private final String path;


	public RedisUsers(final String name)
	{
		this.path = "queue:" + name;
	}


	@Override
	public int size()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			return jedis.zcard(path).intValue();
		}
	}


	@Override
	public long get(final UUID uuid)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			Long rank = jedis.zrank(path, uuid.toString());
			return rank != null ? rank : -1L;
		}
	}

	@Override
	public void del(final UUID uuid)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			jedis.zrem(path, uuid.toString());
		}
	}

	@Override
	public void add(final UUID uuid, final int priority)
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			jedis.zadd(path, Users.determineIndex(jedis.zcard(path).intValue(), priority), uuid.toString());
		}
	}


	@Override
	public Optional<UUID> next()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			final Set<String> values = jedis.zrange(path, 0, 0);
			if (values.isEmpty())
			{
				return Optional.empty();
			}

			return Optional.of(UUID.fromString(values.iterator().next()));
		}
	}


	@Override
	public void reset()
	{
		try (Jedis jedis = RedisBungee.getApi().getPlugin().getPool().getResource())
		{
			jedis.del(path);
		}
	}

}
