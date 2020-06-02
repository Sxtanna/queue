package com.sxtanna.mc.queue.cmds.base;

import com.sxtanna.mc.queue.QueuePlugin;
import net.md_5.bungee.api.plugin.Command;

public abstract class CommandBase extends Command
{

	protected final QueuePlugin plugin;


	protected CommandBase(final String name, final QueuePlugin plugin)
	{
		super(name);
		this.plugin = plugin;
	}

	protected CommandBase(final String name, final String permission, final QueuePlugin plugin, final String... aliases)
	{
		super(name, permission, aliases);
		this.plugin = plugin;
	}

}
