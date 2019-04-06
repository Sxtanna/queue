package org.ipvp.queue.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.ipvp.queue.Queue;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

import static net.md_5.bungee.api.ChatColor.*;

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
                    sender.sendMessage(TextComponent.fromLegacyText(YELLOW + "Debug mode has been set to " + getPlugin().debug));
                }
                else
                {
                    sender.sendMessage(TextComponent.fromLegacyText(RED + "You don't have permission to use /queue debug."));
                }
                return;
            }
            else if (args[0].equals("forget"))
            {
                if(sender.hasPermission("queue.forget"))
                {
                    if(args.length > 1)
                    {
                        boolean found = false;
                        for (Queue queue : getPlugin().getQueues())
                        {
                            if (queue.forgetPlayer(args[1]))
                            {
                                sender.sendMessage(TextComponent.fromLegacyText(YELLOW + "Player position forgotten in the " + queue.getTarget().getName() + " queue."));
                                found = true;
                            }
                        }
                        if(!found)
                        {
                            sender.sendMessage(TextComponent.fromLegacyText(RED + "Player was not saved in any queue, make sure they have left the queue before running this command."));
                        }
                    }
                    else
                    {
                        sender.sendMessage(TextComponent.fromLegacyText(RED + "Invalid arguments."));
                    }
                }
                else
                {
                    sender.sendMessage(TextComponent.fromLegacyText(RED + "You don't have permission to use /queue forget."));
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
                sender.sendMessage(TextComponent.fromLegacyText(RED + "You are not in a queue"));
            }
            else
            {
                sender.sendMessage(TextComponent.fromLegacyText(String.format(YELLOW + "You are currently in position " + GREEN + "%d " + YELLOW + "of " + GREEN + "%d", queuedPlayer.getPosition() + 1, queue.size(), queue.getTarget().getName())));
            }
        }
    }
}
