package com.sxtanna.mc.queue;

import com.google.common.collect.Lists;
import com.sxtanna.mc.queue.cmds.CommandJoin;
import com.sxtanna.mc.queue.cmds.CommandLeave;
import com.sxtanna.mc.queue.cmds.CommandPause;
import com.sxtanna.mc.queue.cmds.CommandQueue;
import com.sxtanna.mc.queue.hook.RedisBungeeHook;
import com.sxtanna.mc.queue.impl.Queue;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class QueuePlugin extends Plugin
{

	private static final long REFRESH_QUEUE_TIME = 10_000L;

	private static final String CONFIG_FILE_NAME = "config.yml";


	private final Map<ServerInfo, Queue>   serverQueues = new ConcurrentHashMap<>();
	private final Map<ServerInfo, Integer> serverLimits = new ConcurrentHashMap<>();

	private final Map<String, Integer> playerPriorities = new LinkedHashMap<>();


	private Configuration   conf;
	private ScheduledTask   task;
	private RedisBungeeHook hook;


	@Override
	public void onEnable()
	{
		try
		{
			saveConfiguration();
		}
		catch (final IOException ex)
		{
			return;
		}

		loadConfiguration();

		refreshServerLimits();

		final PluginManager manager = getProxy().getPluginManager();

		if (manager.getPlugin("RedisBungee") != null)
		{
			getLogger().info("attempting to hook into RedisBungee");

			try
			{
				hook = new RedisBungeeHook();
			}
			catch (final Throwable ex)
			{
				getLogger().log(Level.SEVERE, "failed to register RedisBungee hook", ex);
			}
		}

		task = getProxy().getScheduler().schedule(this, this::refreshServerQueues, 0L, REFRESH_QUEUE_TIME, TimeUnit.MILLISECONDS);


		manager.registerCommand(this, new CommandJoin(this));
		manager.registerCommand(this, new CommandLeave(this));
		manager.registerCommand(this, new CommandPause(this));
		manager.registerCommand(this, new CommandQueue(this));
	}

	@Override
	public void onDisable()
	{
		if (task != null)
		{
			task.cancel();
		}

		conf = null;
		task = null;
		hook = null;

		serverQueues.values().forEach(Queue::reset);

		serverQueues.clear();
		serverLimits.clear();
	}


	public boolean usingRedisBungee()
	{
		return hook != null && hook.usable();
	}


	public int getServerPlayerCount(final ServerInfo server)
	{
		if (usingRedisBungee())
		{
			int count = hook.getPlayerCount(server);

			if (count != -1)
			{
				return count;
			}
		}

		return server.getPlayers().size();
	}

	public int getServerPlayerLimit(final ServerInfo server)
	{
		final Integer limit = serverLimits.get(server);

		return limit != null ? limit : -1;
	}


	public int getPlayerPriority(final ProxiedPlayer player)
	{
		// linked hashmap maintains insertion order
		for (final Map.Entry<String, Integer> entry : playerPriorities.entrySet())
		{
			if (player.hasPermission("queue.priority." + entry.getKey()))
			{
				return entry.getValue();
			}
		}

		return 0;
	}


	public Queue getServerQueue(final ServerInfo server)
	{
		return serverQueues.computeIfAbsent(server, ($) -> new Queue(this, server));
	}

	public Optional<Queue> getPlayerQueue(final UUID uuid)
	{
		for (final Queue value : serverQueues.values())
		{
			if (value.contains(uuid))
			{
				return Optional.of(value);
			}
		}

		return Optional.empty();
	}


	public Optional<ServerInfo> getServerByName(final String name)
	{
		for (final Map.Entry<String, ServerInfo> entry : getProxy().getServers().entrySet())
		{
			if (entry.getKey().equalsIgnoreCase(name))
			{
				return Optional.of(entry.getValue());
			}
		}

		return Optional.empty();
	}


	public void reload() throws IOException
	{
		refreshServerLimits();
		saveConfiguration();
		loadConfiguration();
	}


	private void refreshServerQueues()
	{
		getProxy().getServers().forEach(($, info) -> getServerQueue(info));

		for (final Queue queue : serverQueues.values())
		{
			try
			{
				queue.poll();
			}
			catch (final Exception ex)
			{
				getLogger().log(Level.SEVERE, "failed to process queue for server " + queue.getTarget().getName(), ex);
			}
		}
	}

	private void refreshServerLimits()
	{
		getProxy().getServers().forEach(($, info) -> {
			info.ping((ping, fail) -> {
				if (ping != null && ping.getPlayers() != null)
				{
					serverLimits.put(info, ping.getPlayers().getMax());
				}
			});
		});
	}


	private void saveConfiguration() throws IOException
	{
		final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

		final File file = new File(getDataFolder(), CONFIG_FILE_NAME);
		if (file.exists())
		{
			try
			{
				this.conf = provider.load(file);
				return;
			}
			catch (final IOException ex)
			{
				getLogger().log(Level.SEVERE, "failed to load configuration from file", ex);
				throw ex;
			}
		}
		else
		{
			try
			{
				//noinspection ResultOfMethodCallIgnored
				file.getParentFile().mkdirs();
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
			}
			catch (final IOException ex)
			{
				getLogger().log(Level.SEVERE, "failed to create config file", ex);
				throw ex;
			}
		}


		this.conf = provider.load(getResourceAsStream(CONFIG_FILE_NAME));

		try
		{
			provider.save(conf, file);
		}
		catch (final IOException ex)
		{
			getLogger().log(Level.SEVERE, "failed to save default config", ex);
			throw ex;
		}
	}

	private void loadConfiguration()
	{
		if (conf == null)
		{
			return;
		}


		final List<String> priorities = Lists.newArrayList(conf.getSection("priorities").getKeys());
		// sort priorities, highest to lowest
		priorities.sort(Comparator.comparingInt(name -> conf.getInt("priorities." + name)).reversed());

		for (final String priority : priorities)
		{
			playerPriorities.put(priority, conf.getInt("priorities." + priority));
		}
	}

}
