package com.sxtanna.mc.queue.prev.trash.queue.command;

import net.md_5.bungee.api.plugin.Command;
import com.sxtanna.mc.queue.prev.trash.queue.QueuePlugin;

public abstract class QueuePluginCommand extends Command
{
    protected QueuePlugin plugin;

    public QueuePluginCommand(QueuePlugin plugin, String name)
    {
        super(name);
        this.plugin = plugin;
    }

    public QueuePluginCommand(QueuePlugin plugin, String name, String permission, String... aliases)
    {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    public QueuePlugin getPlugin() {
        return plugin;
    }
}
