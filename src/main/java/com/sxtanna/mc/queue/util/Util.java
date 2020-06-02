package com.sxtanna.mc.queue.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.function.Consumer;

public enum Util
{
	;

	public static void reply(final CommandSender sender, final Consumer<ComponentBuilder> consumer)
	{
		final ComponentBuilder builder = new ComponentBuilder();
		consumer.accept(builder);

		sender.sendMessage(builder.create());
	}
}
