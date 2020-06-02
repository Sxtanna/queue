package com.sxtanna.mc.queue.impl.base;

import java.util.Optional;
import java.util.UUID;

public interface Users
{

	int size();


	long get(final UUID uuid);

	void del(final UUID uuid);

	void add(final UUID uuid, final int priority);


	Optional<UUID> next();


	void reset();


	static int determineIndex(final int length, final int priority)
	{
		return (int) Math.max(0.0, length - (Math.max(0, priority) / 100.0 * length));
	}

}