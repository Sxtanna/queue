package com.sxtanna.mc.queue.cmds;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.cmds.base.CommandBase;
import com.sxtanna.mc.queue.impl.Queue;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class CommandQueue extends CommandBase
{

	private final Map<String, BiConsumer<CommandSender, String[]>> commands = new HashMap<>();


	public CommandQueue(final QueuePlugin plugin)
	{
		super("queue", plugin);

		commands.put("reload", this::commandReload);
	}


	@Override
	public void execute(final CommandSender sender, final String[] args)
	{
		if (args.length == 0)
		{
			if (!(sender instanceof ProxiedPlayer))
			{
				Util.reply(sender, builder ->
						builder.append("You must be a player to do this").color(ChatColor.RED));
				return;
			}

			final ProxiedPlayer   player = (ProxiedPlayer) sender;
			final Optional<Queue> queue  = plugin.getPlayerQueue(player.getUniqueId());

			if (!queue.isPresent())
			{
				Util.reply(sender, builder ->
						builder.append("You are not in a queue").color(ChatColor.RED));
				return;
			}

			final int  max = queue.get().getQueuedUsers().size();
			final long cur = queue.get().getQueuedUsers().get(player.getUniqueId()) + 1;

			Util.reply(sender, builder ->
			{
				builder.append("You are currently in position ").color(ChatColor.YELLOW);
				builder.append(String.valueOf(cur)).color(ChatColor.GREEN);
				builder.append(" of ").color(ChatColor.YELLOW);
				builder.append(String.valueOf(max)).color(ChatColor.GREEN);
				builder.append(" for ").color(ChatColor.YELLOW);
				builder.append(queue.get().getTarget().getName()).color(ChatColor.AQUA);
			});
			return;
		}


		final BiConsumer<CommandSender, String[]> command = commands.get(args[0].toLowerCase());
		if (command == null)
		{
			Util.reply(sender, builder ->
					builder.append("That command doesn't exist").color(ChatColor.RED));
			return;
		}

		if (!sender.hasPermission("queue." + args[0].toLowerCase()))
		{
			Util.reply(sender, builder ->
					builder.append("You don't have permission to do this").color(ChatColor.RED));
			return;
		}

		command.accept(sender, Arrays.copyOfRange(args, 1, args.length));
	}


	private void commandReload(final CommandSender sender, final String[] args)
	{
		try
		{
			plugin.reload();
		}
		catch (final IOException ex)
		{
			Util.reply(sender, builder ->
			{
				builder.append("failed to reload plugin: ").color(ChatColor.RED);
				builder.append(ex.getMessage()).color(ChatColor.DARK_RED);
			});
			return;
		}

		Util.reply(sender, builder ->
				builder.append("reloaded servers and configuration").color(ChatColor.GREEN));
	}

}
