package com.sxtanna.mc.queue.cmds;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.impl.Queue;
import com.sxtanna.mc.queue.cmds.base.CommandBase;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;

public final class CommandJoin extends CommandBase
{

	public CommandJoin(final QueuePlugin plugin)
	{
		super("joinqueue", plugin);
	}


	@Override
	public void execute(final CommandSender sender, final String[] args)
	{
		if (!(sender instanceof ProxiedPlayer))
		{
			Util.reply(sender, builder ->
					builder.append("You must be a player to do this").color(ChatColor.RED));
			return;
		}

		final ProxiedPlayer player = (ProxiedPlayer) sender;

		if (args.length != 1)
		{
			Util.reply(sender, builder ->
			{
				builder.append("Invalid arguments: ").color(ChatColor.RED);
				builder.append("/joinqueue ").color(ChatColor.GRAY);
				builder.append("<server>").color(ChatColor.AQUA);
			});
			return;
		}

		final Optional<ServerInfo> server = plugin.getServerByName(args[0]);
		if (!server.isPresent())
		{
			Util.reply(sender, builder ->
			{
				builder.append("Invalid server name: ").color(ChatColor.RED);
				builder.append(args[0]).color(ChatColor.YELLOW);
			});
			return;
		}

		if (!sender.hasPermission("queue.join." + args[0]) && !sender.hasPermission("queue.join.*"))
		{
			Util.reply(sender, builder ->
					builder.append("You don't have permission to queue for this server!").color(ChatColor.RED));
			return;
		}

		if (player.getServer().getInfo().equals(server.get()))
		{
			Util.reply(sender, builder ->
					builder.append("You are already connected to this server!").color(ChatColor.RED));
			return;
		}

		final Queue serverQueue = plugin.getServerQueue(server.get());
		serverQueue.join(player);
	}

}
