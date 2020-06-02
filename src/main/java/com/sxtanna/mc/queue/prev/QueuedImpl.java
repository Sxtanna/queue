/*
package com.sxtanna.mc.queue.prev;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.prev.local.LocalQueuedList;
import com.sxtanna.mc.queue.prev.redis.RedisQueuedList;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class QueuedImpl implements Queued
{

	private static final long SEND_FAILURE_LIMIT = 5L;
	private static final long SEND_PLAYERS_DELAY = 1000L;


	private final QueuePlugin plugin;

	private final ServerInfo server;
	private final QueuedList queued;


	private int sendFailCount;

	private boolean pauseSend;
	private long    unpauseAt;

	private boolean handlingNextSend;
	private long    lastPlayerSentAt;


	private long lastUpdateSentAt;


	public QueuedImpl(final QueuePlugin plugin, final ServerInfo server)
	{
		this.plugin = plugin;

		this.server = server;
		this.queued = !plugin.usingRedisBungee() ? new LocalQueuedList() : new RedisQueuedList(server.getName());
	}


	@Override
	public ServerInfo getServer()
	{
		return server;
	}

	@Override
	public QueuedList getQueued()
	{
		return queued;
	}


	@Override
	public void reset()
	{
		queued.vacate();

		sendFailCount = 0;

		pauseSend = false;
		unpauseAt = 0;

		handlingNextSend = false;
		lastPlayerSentAt = 0;

		lastPlayerSentAt = 0;
	}


	@Override
	public boolean canSend()
	{
		System.out.println("cansend 0");
		if (pauseSend && (unpauseAt <= System.currentTimeMillis()))
		{
			// automatically unpause when the time has passed

			unpauseAt = 0;
			pauseSend = false;
		}

		System.out.println("cansend 1");

		if (pauseSend)
		{
			return false; // sending is currently paused
		}

		System.out.println("cansend 2");

		if (handlingNextSend)
		{
			return false; // awaiting current send response
		}

		System.out.println("cansend 3");

		if (getQueued().vacant())
		{
			return false; // queue is empty
		}

		System.out.println("cansend 4");

		if ((lastPlayerSentAt + SEND_PLAYERS_DELAY) >= System.currentTimeMillis())
		{
			return false; // last player was sent within the delay
		}

		System.out.println("cansend 5");

		return plugin.getServerPlayerCount(getServer()) < plugin.getServerPlayerLimit(getServer());
	}

	@Override
	public boolean hasUser(final UUID uuid)
	{
		return getQueued().getPosition(uuid) != QueuedList.IS_NOT_QUEUED;
	}


	@Override
	public void sendUser()
	{
		System.out.println("senduser 0");

		if (!canSend())
		{
			return; // not ready to send another player
		}

		System.out.println("senduser 1");

		if (sendFailCount >= SEND_FAILURE_LIMIT)
		{

			sendFailCount = 0;

			pauseSendFor(30, TimeUnit.SECONDS);

			return; // failure threshold reached, pause sending for 30 seconds
		}

		System.out.println("senduser 2");


		final Optional<UUID> next = getQueued().next();
		if (!next.isPresent())
		{
			return; // no queued players
		}

		System.out.println("senduser 4: " + next);

		final ProxiedPlayer player = plugin.getProxy().getPlayer(next.get());
		if (player == null)
		{
			return; // that player isn't on this proxy
		}

		System.out.println("senduser 5");


		getQueued().delete(next.get());


		if (player.getServer().getInfo().equals(getServer()))
		{
			return; // they are already connected
		}

		System.out.println("senduser 6");

		handlingNextSend = true;

		// attempt to connect them to the target
		player.connect(getServer(), (pass, fail) -> handleConnect(player, pass, fail), ServerConnectEvent.Reason.PLUGIN);
	}

	@Override
	public void joinUser(final ProxiedPlayer player)
	{
		final Optional<Queued> previous = plugin.getPlayerQueue(player.getUniqueId());
		if (previous.isPresent())
		{
			if (previous.get().equals(this))
			{
				Util.reply(player, builder ->
						builder.append("You are already queued for this server!").color(ChatColor.RED));
				return;
			}

			previous.get().quitUser(player);

			Util.reply(player, builder ->
			{
				builder.append("You were removed from the queue of ").color(ChatColor.RED);
				builder.append(previous.get().getServer().getName()).color(ChatColor.YELLOW);
			});
		}

		getQueued().setPosition(player.getUniqueId(), getPositionOf(player));
	}

	@Override
	public void quitUser(final ProxiedPlayer player)
	{
		getQueued().delete(player.getUniqueId());
	}


	private void pauseSendFor(final long time, final TimeUnit unit)
	{
		pauseSend = true;
		unpauseAt = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit);
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

		getQueued().setPosition(player.getUniqueId(), 0);
	}


	private long getPositionOf(final ProxiedPlayer player)
	{
		final int queued   = getQueued().size();
		final int priority = plugin.getPlayerPriority(player);

		if (queued == 0 || priority > 100)
		{
			return 0L;
		}

		final long position = getQueued().last().map(getQueued()::getPosition).orElse(((long) queued));

		return (long) Math.max(0.0, position - ((priority / 100.0) * position));
	}


	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof QueuedImpl))
		{
			return false;
		}

		final QueuedImpl queued = (QueuedImpl) o;
		return getServer().equals(queued.getServer());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getServer());
	}

	@Override
	public String toString()
	{
		return String.format("QueuedImpl:%s", getServer().getName());
	}

}
*/
