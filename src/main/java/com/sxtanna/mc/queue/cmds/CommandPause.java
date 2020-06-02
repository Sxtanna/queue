package com.sxtanna.mc.queue.cmds;

import com.sxtanna.mc.queue.QueuePlugin;
import com.sxtanna.mc.queue.cmds.base.CommandBase;
import com.sxtanna.mc.queue.impl.Queue;
import com.sxtanna.mc.queue.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class CommandPause extends CommandBase
{

	private static final long     PAUSE_TIME = 24;
	private static final TimeUnit PAUSE_UNIT = TimeUnit.HOURS;


	public CommandPause(final QueuePlugin plugin)
	{
		super("pausequeue", "queue.pause", plugin);
	}


	@Override
	public void execute(final CommandSender sender, final String[] args)
	{
		if (args.length != 1)
		{
			Util.reply(sender, builder ->
			{
				builder.append("Invalid arguments: ").color(ChatColor.RED);
				builder.append("/pausequeue ").color(ChatColor.GRAY);
				builder.append("<server>").color(ChatColor.AQUA);
			});
			return;
		}

		final Optional<Queue> queue = plugin.getServerByName(args[0]).map(plugin::getServerQueue);
		if (!queue.isPresent())
		{
			Util.reply(sender, builder ->
					builder.append("There is no queue for that server").color(ChatColor.RED));
			return;
		}

		queue.get().pauseSendFor(queue.get().isPaused() ? -1 : PAUSE_TIME, PAUSE_UNIT);

		Util.reply(sender, builder ->
		{
			final boolean paused = queue.get().isPaused();

			builder.append("You have ").color(ChatColor.GRAY);
			builder.append(paused ? "paused" : "resumed").color(ChatColor.GREEN);
			builder.append(" the queue for ").color(ChatColor.GRAY);
			builder.append(queue.get().getTarget().getName()).color(ChatColor.AQUA);
		});
	}

}
