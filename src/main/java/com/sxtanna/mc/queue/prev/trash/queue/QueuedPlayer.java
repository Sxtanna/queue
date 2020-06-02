package com.sxtanna.mc.queue.prev.trash.queue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;

public final class QueuedPlayer {

    private ProxiedPlayer handle;
    private int priority;
    private Queue queue;

    public QueuedPlayer(ProxiedPlayer handle, int priority)
    {
        Objects.requireNonNull(handle, "Player cannot be null");
        this.handle = handle;
        this.priority = priority;
    }

    /**
     * Returns the ProxiedPlayer represented by this instance
     *
     * @return ProxiedPlayer handle
     */
    public ProxiedPlayer getHandle()
    {
        return handle;
    }

    /**
     * Returns the priority rank information about this player
     *
     * @return Priority information
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Returns the current queue this player is in
     *
     * @return Queue entered, or null
     */
    public Queue getQueue()
    {
        return queue;
    }

    /**
     * Returns whether or not the player is in a queue
     *
     * @return True if the player is in a queue, false otherwise
     */
    public boolean isInQueue()
    {
        if(queue != null)
        {
            if(!queue.contains(this))
            {
                QueuePlugin.instance.debugError("Player were set as in a queue but could not be found within it.");
                getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occured in the queue, you are set as queued to " + QueuePlugin.capitalizeFirstLetter(queue.getTarget().getName()) + " but you were not found in the queue."));
                getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Attempting to requeue you in your last known position..."));
                queue.enqueue(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the current position inside the queue the player is waiting for
     *
     * @return Queue position
     * @throws IllegalStateException When the player is not in a queue
     */
    public int getPosition()
    {
        if (!isInQueue())
        {
            QueuePlugin.instance.debugWarn("Tried to check " + getHandle().getName() + "'s position but they were not in a queue.");
            return -1;
        }
        return queue.indexOf(this);
    }

    /**
     * Sets the queue this player is waiting for
     *
     * @param queue New queue to wait in
     */
    public void setQueue(Queue queue)
    {
        this.queue = queue;
    }

    @Override
    public int hashCode()
    {
        int prime = 31;
        return prime * handle.hashCode() + prime * priority;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof QueuedPlayer))
        {
            return false;
        }
        QueuedPlayer other = (QueuedPlayer) o;
        return other.getHandle().equals(handle) && other.priority == priority;
    }
}
