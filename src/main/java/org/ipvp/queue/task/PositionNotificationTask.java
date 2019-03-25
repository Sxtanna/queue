package org.ipvp.queue.task;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

import static net.md_5.bungee.api.ChatColor.*;

public class PositionNotificationTask implements Runnable
{
    private QueuePlugin plugin;

    public PositionNotificationTask(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        plugin.getQueued().stream().filter(QueuedPlayer::isInQueue).forEach(p ->
        {
            p.getHandle().sendMessage(TextComponent.fromLegacyText(String.format(YELLOW + "You are currently in position " + GREEN + "%d " + YELLOW + "of " + GREEN + "%d " + YELLOW + "for EarthMC",
                    p.getPosition() + 1, p.getQueue().size(), p.getQueue().getTarget().getName())));

            // EMC Specific roles
            if (p.getHandle().hasPermission("queue.priority.staff"))
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(DARK_GREEN + "Staff" + GREEN + " access access activated."));
            }
            else if (p.getHandle().hasPermission("queue.priority.donator3"))
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(BLUE + "Blue" + GREEN + " donator access activated."));

            }
            else if (p.getHandle().hasPermission("queue.priority.donator2"))
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(LIGHT_PURPLE + "Purple" + GREEN + " donator access activated."));
            }
            else if (p.getHandle().hasPermission("queue.priority.donator"))
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(YELLOW + "Yellow" + GREEN + " donator access activated."));
            }
            else if (p.getHandle().hasPermission("queue.priority.priority"))
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(GREEN + "Priority access activated."));
            }

            if (p.getQueue().isPaused())
            {
                p.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "The queue you are currently in is paused"));
            }
        });
    }
}
