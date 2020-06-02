package com.sxtanna.mc.queue.cmds;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.cmds.base.CommandBase;
import com.sxtanna.mc.queue.impl.Queue;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;

public final class CommandLeave extends CommandBase
{

	public CommandLeave(final QueuePlugin plugin)
	{
		super("leavequeue", plugin);
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

		final Optional<Queue> queue = plugin.getPlayerQueue(player.getUniqueId());
		if (!queue.isPresent())
		{
			Util.reply(sender, builder ->
					builder.append("You are not in a queue").color(ChatColor.RED));
			return;
		}

		queue.get().quit(player);
	}

}
