package com.sxtanna.mc.queue.impl.impl;

import com.sxtanna.mc.queue.impl.base.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class LocalUsers implements Users
{

	private final List<UUID> index = new ArrayList<>();


	@Override
	public int size()
	{
		return index.size();
	}

	@Override
	public long get(final UUID uuid)
	{
		return index.indexOf(uuid);
	}

	@Override
	public void del(final UUID uuid)
	{
		index.remove(uuid);
	}

	@Override
	public void add(final UUID uuid, final int priority)
	{
		index.add(Users.determineIndex(size(), priority), uuid);
	}

	@Override
	public Optional<UUID> next()
	{
		if (index.isEmpty())
		{
			return Optional.empty();
		}

		return Optional.ofNullable(index.get(0));
	}


	@Override
	public void reset()
	{
		index.clear();
	}

}
