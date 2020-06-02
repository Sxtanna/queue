/*
package com.sxtanna.mc.queue.prev.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sxtanna.mc.queue.prev.QueuedList;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class LocalQueuedList implements QueuedList
{

	private final Cache<UUID, Long> positions = CacheBuilder.newBuilder()
															.expireAfterWrite(QueuedList.EXPIRES_AFTER, TimeUnit.MILLISECONDS)
															.build();

	@Override
	public int size()
	{
		return positions.asMap().size();
	}

	@Override
	public long getPosition(final UUID uuid)
	{
		final Long position = positions.getIfPresent(uuid);
		return position != null ? position : QueuedList.IS_NOT_QUEUED;
	}

	@Override
	public long setPosition(final UUID uuid, final long position)
	{
		final Long previous = positions.asMap().put(uuid, position);
		return previous != null ? previous : QueuedList.IS_NOT_QUEUED;
	}


	@Override
	public boolean vacant()
	{
		return positions.asMap().isEmpty();
	}

	@Override
	public boolean delete(final UUID uuid)
	{
		return positions.asMap().remove(uuid) != null;
	}


	@Override
	public Optional<UUID> next()
	{
		if (vacant())
		{
			return Optional.empty();
		}

		Map.Entry<UUID, Long> next = null;

		for (final Map.Entry<UUID, Long> entry : positions.asMap().entrySet())
		{
			if (next == null || entry.getValue() < next.getValue())
			{
				next = entry;
			}
		}

		return next == null ? Optional.empty() : Optional.of(next.getKey());
	}

	@Override
	public Optional<UUID> last()
	{
		if (vacant())
		{
			return Optional.empty();
		}

		Map.Entry<UUID, Long> last = null;

		for (final Map.Entry<UUID, Long> entry : positions.asMap().entrySet())
		{
			if (last == null || entry.getValue() > last.getValue())
			{
				last = entry;
			}
		}

		return last == null ? Optional.empty() : Optional.of(last.getKey());
	}

	@Override
	public void vacate()
	{
		positions.invalidateAll();
	}

}
*/
