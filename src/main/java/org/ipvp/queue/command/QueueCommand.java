package org.ipvp.queue.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.ipvp.queue.Queue;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

public class QueueCommand extends QueuePluginCommand
{
    public QueueCommand(QueuePlugin plugin)
    {
        super(plugin, "queue");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length > 0)
        {
            // /queue debug
            if(args[0].equals("debug"))
            {
                if(sender.hasPermission("queue.debug"))
                {
                    getPlugin().debug = !getPlugin().debug;
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + "Debug mode has been set to " + getPlugin().debug));
                }
                else
                {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You don't have permission to use /queue debug."));
                }
                return;
            }
        }


        // All other /queue variations
        if (!(sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(TextComponent.fromLegacyText("You must be a player to use this command"));
        }
        else
        {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            QueuedPlayer queuedPlayer = getPlugin().getQueued(player);

            Queue queue = queuedPlayer.getQueue();

            if (queue == null)
            {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are not in a queue"));
            }
            else
            {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + String.format("You are currently in position %d of %d for server %s", queuedPlayer.getPosition() + 1, queue.size(), queue.getTarget().getName())));
            }
        }
    }
}
