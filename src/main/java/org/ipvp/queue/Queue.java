package org.ipvp.queue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Queue extends ArrayList<QueuedPlayer>
{
    /**
     * The time between ticking the queue to send a new player
     */
    public static final long TIME_BETWEEN_SENDING_MILLIS = 500L;

    private final QueuePlugin plugin;
    private final ServerInfo target;
    private boolean paused;
    private long lastSentTime;
    private HashMap<String, Integer> savedPositions = new HashMap<>();

    public Queue(QueuePlugin plugin, ServerInfo target)
    {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(target);
        this.plugin = plugin;
        this.target = target;
    }

    /**
     * Returns the target server for this queue.
     *
     * @return Target server
     */
    public final ServerInfo getTarget() {
        return target;
    }

    /**
     * Returns whether this queue is paused or not.
     *
     * @return True if the queue is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the paused state of this queue.
     *
     * @param paused New paused state
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Returns whether this queue can send the next player to the target server. This
     * method will only return true when the queue is not paused, has a player to send,
     * when the target server has space for the player, and if a specific interval has
     * passed since the last time a player was sent.
     *
     * @return True if the queue can send the next player, false otherwise
     */
    public boolean canSend()
    {
        return !isPaused() &&
                !isEmpty() &&
                target.getPlayers().size() < plugin.getMaxPlayers(target) &&
                lastSentTime + TIME_BETWEEN_SENDING_MILLIS < System.currentTimeMillis();
    }

    /**
     * Saves players position in the queue when they leave so they can get it back if they rejoin
     * @param playerName The player to save
     */
    public void savePlayerPosition(String playerName)
    {
        savePlayerPosition(playerName, 0);
    }

    public void savePlayerPosition(String playerName, int index)
    {
        savedPositions.put(playerName.toLowerCase(), index);
        plugin.getProxy().getScheduler().schedule(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    savedPositions.remove(playerName.toLowerCase());
                }
                catch (Exception e)
                {
                    if(plugin.debug)
                    {
                        plugin.getLogger().log(Level.WARNING, "Failed to delete player from savedPositions, this is probably fine. " + e.getMessage());
                    }
                }
            }
        }, 15L,  TimeUnit.MINUTES);
    }

    public boolean forgetPlayer(String playerName)
    {
        return savedPositions.remove(playerName.toLowerCase()) != null;
    }

    /**
     * Checks if a player has recently been in the queue and returns their old position or the end of the queue if so
     * @param player The player to look up
     * @return -1 if the player is not listed, the position for them to be inserted into otherwise
     */
    public int getSavedIndex(ProxiedPlayer player)
    {
        if(savedPositions.containsKey(player.getName().toLowerCase()))
        {
            if (savedPositions.get(player.getName().toLowerCase()) < this.size())
            {
                return savedPositions.get(player.getName().toLowerCase());
            }
            else
            {
                return this.size();
            }
        }
        return -1;
    }

    /**
     * Searches for and returns a valid index to insert a player with a specified
     * priority weight.
     *
     * @param weight Priority weight to search for
     * @return Index to insert the priority at, returned index i will be {@code 0 <= i < {@link #size()}}
     */
    public int getIndexFor(int weight)
    {
        if (isEmpty() || weight == -1)
        {
            return 0;
        }

        // Changed to not place priority players in the first 3 slots
        for (int i = 5; i < size(); i++)
        {
            if (weight > get(i).getPriority())
            {
                return i;
            }
        }
        return size();
    }

    /**
     * Sends the next player at index {@code 0} to the target server.
     */
    public void sendNext()
    {
        if (!canSend())
        {
            throw new IllegalStateException("Cannot send next player in queue");
        }

        QueuedPlayer next = remove(0);
        next.setQueue(null);
        savePlayerPosition(next.getHandle().getName());
        next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Sending you to EarthMC..."));

        plugin.getLogger().log(Level.INFO, next.getHandle().getName() + " was sent to " + target.getName() + " via Queue.");
        next.getHandle().connect(target, (result, error) ->
        {
            // What do we do if they can't connect?
            if (result)
            {
                next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "You have been sent to EarthMC"));
                lastSentTime = System.currentTimeMillis();
            }
            else
            {
                next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unable to connect to EarthMC."));
            }
        });
    }
}