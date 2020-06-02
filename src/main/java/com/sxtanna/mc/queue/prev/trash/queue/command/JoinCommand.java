package com.sxtanna.mc.queue.prev.trash.queue.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.sxtanna.mc.queue.prev.trash.queue.Queue;
import com.sxtanna.mc.queue.prev.trash.queue.QueuePlugin;
import com.sxtanna.mc.queue.prev.trash.queue.QueuedPlayer;

import static net.md_5.bungee.api.ChatColor.RED;

public class JoinCommand extends QueuePluginCommand
{
	public JoinCommand(QueuePlugin plugin)
	{
		super(plugin, "joinqueue");
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if(!(sender instanceof ProxiedPlayer))
		{
			sender.sendMessage(TextComponent.fromLegacyText(RED + "You cannot use this from the console."));
			return;
		}

		if(args.length < 1)
		{
			sender.sendMessage(TextComponent.fromLegacyText(RED + "Invalid arguments."));
			return;
		}

		if(!sender.hasPermission("queue.join." + args[0]) && !sender.hasPermission("queue.join.*"))
		{
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You don't have permission to use this command!"));
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer)sender;
		QueuedPlayer queuedPlayer = getPlugin().getQueued(proxiedPlayer);
		String target = args[0].toLowerCase();

		if(proxiedPlayer.getServer().getInfo().getName().equals(target))
		{
			proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already on this server!"));
			return;
		}
		Queue queue = getPlugin().getQueue(target);

		if (queue == null)
		{
			ServerInfo server = getPlugin().getProxy().getServerInfo(target); // Find server
			if (server == null)
			{
				proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid server provided"));
				return;
			}
			else
			{
				queue = new Queue(getPlugin(), server);
				getPlugin().queues.put(server.getName(), queue);
			}
		}
		queue.enqueue(queuedPlayer);
	}
}
