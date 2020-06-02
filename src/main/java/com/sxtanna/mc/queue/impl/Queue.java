package com.sxtanna.mc.queue.impl;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.impl.base.Users;
import com.sxtanna.mc.queue.impl.impl.LocalUsers;
import com.sxtanna.mc.queue.impl.impl.RedisUsers;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Queue
{

	private static final long SEND_FAILURE_LIMIT = 5L;
	private static final long SEND_PLAYERS_DELAY = 1000L;


	private final QueuePlugin plugin;


	private final ServerInfo target;
	private final Users      queued;


	private int sendFailCount;

	private boolean pauseSend;
	private long    unpauseAt;

	private boolean handlingNextSend;
	private long    lastPlayerSentAt;


	private long lastUpdateSentAt;


	public Queue(final QueuePlugin plugin, final ServerInfo target)
	{
		this.plugin = plugin;

		this.target = target;
		this.queued = !plugin.usingRedisBungee() ? new LocalUsers() : new RedisUsers(target.getName());
	}


	public ServerInfo getTarget()
	{
		return target;
	}

	public Users getQueuedUsers()
	{
		return queued;
	}


	public void poll()
	{
		if (!sendable())
		{
			return; // not ready to send another player
		}

		if (sendFailCount >= SEND_FAILURE_LIMIT)
		{

			sendFailCount = 0;

			pauseSendFor(30, TimeUnit.SECONDS);

			return; // failure threshold reached, pause sending for 30 seconds
		}

		final Optional<UUID> next = getQueuedUsers().next();
		if (!next.isPresent())
		{
			return; // no queued players
		}

		final ProxiedPlayer player = plugin.getProxy().getPlayer(next.get());
		if (player == null)
		{
			return; // that player isn't on this proxy
		}

		getQueuedUsers().del(player.getUniqueId());


		if (player.getServer().getInfo().equals(getTarget()))
		{
			return; // they are already connected
		}


		handlingNextSend = true;

		// attempt to connect them to the target
		player.connect(getTarget(), (pass, fail) -> handleConnect(player, pass, fail));
	}

	public void join(final ProxiedPlayer player)
	{
		final Optional<Queue> prev = plugin.getPlayerQueue(player.getUniqueId());
		if (prev.isPresent())
		{
			if (prev.get().equals(this))
			{
				Util.reply(player, builder ->
						builder.append("You are already queued for this server!").color(ChatColor.RED));
				return;
			}

			prev.get().quit(player);

			Util.reply(player, builder ->
			{
				builder.append("You were removed from the queue of ").color(ChatColor.RED);
				builder.append(prev.get().getTarget().getName()).color(ChatColor.YELLOW);
			});
		}

		getQueuedUsers().add(player.getUniqueId(), plugin.getPlayerPriority(player));

		Util.reply(player, builder ->
		{
			builder.append("You have joined the queue of ").color(ChatColor.GREEN);
			builder.append(getTarget().getName()).color(ChatColor.AQUA);
		});
	}

	public void quit(final ProxiedPlayer player)
	{
		getQueuedUsers().del(player.getUniqueId());

		Util.reply(player, builder ->
		{
			builder.append("You have left the queue of ").color(ChatColor.GREEN);
			builder.append(getTarget().getName()).color(ChatColor.AQUA);
		});
	}


	public boolean sendable()
	{
		if (pauseSend && (unpauseAt <= System.currentTimeMillis()))
		{
			// automatically unpause when the time has passed

			unpauseAt = 0;
			pauseSend = false;
		}

		if (pauseSend)
		{
			return false; // sending is currently paused
		}

		if (handlingNextSend)
		{
			return false; // awaiting current send response
		}

		if (getQueuedUsers().size() == 0)
		{
			return false; // queue is empty
		}

		if ((lastPlayerSentAt + SEND_PLAYERS_DELAY) >= System.currentTimeMillis())
		{
			return false; // last player was sent within the delay
		}

		return plugin.getServerPlayerCount(getTarget()) < plugin.getServerPlayerLimit(getTarget());
	}

	public boolean contains(final UUID uuid)
	{
		return getQueuedUsers().get(uuid) != -1L;
	}


	public boolean isPaused()
	{
		return pauseSend;
	}

	public void pauseSendFor(final long time, final TimeUnit unit)
	{
		if (time == -1) // magic value.. bad Sxtanna ¯\_(ツ)_/¯
		{
			pauseSend = false;
			unpauseAt = 0;
		}
		else
		{
			pauseSend = true;
			unpauseAt = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit);
		}
	}


	public void reset()
	{
		getQueuedUsers().reset();

		sendFailCount = 0;

		pauseSend = false;
		unpauseAt = 0;

		handlingNextSend = false;
		lastPlayerSentAt = 0;

		lastPlayerSentAt = 0;
	}


	private void handleConnect(final ProxiedPlayer player, final boolean pass, final Throwable fail)
	{
		handlingNextSend = false;

		if (pass)
		{
			lastPlayerSentAt = System.currentTimeMillis();
			return;
		}

		sendFailCount++;

		getQueuedUsers().add(player.getUniqueId(), 0);
	}


	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Queue))
		{
			return false;
		}

		final Queue queue = (Queue) o;
		return getTarget().equals(queue.getTarget());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getTarget());
	}

	@Override
	public String toString()
	{
		return String.format("Queue:%s", getTarget().getName());
	}

}
