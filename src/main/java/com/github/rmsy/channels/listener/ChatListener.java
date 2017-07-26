package com.github.rmsy.channels.listener;

import com.github.rmsy.channels.Channel;
import com.github.rmsy.channels.ChannelsPlugin;
import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

/** Listener for chat-related events. */
public class ChatListener implements Listener {

    /** The plugin. */
    private final ChannelsPlugin plugin;

    private ChatListener() {
        this.plugin = null;
    }

    /**
     * Creates a new ChatListener.
     *
     * @param plugin The plugin.
     */
    public ChatListener(final ChannelsPlugin plugin) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(final PlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage().trim();
        if (message.indexOf('!') == 0) {
            event.setCancelled(true);
            Channel globalChannel = this.plugin.getGlobalChannel();
            if (message.length() > 1) {
                globalChannel.sendMessage(message.substring(1, message.length()).trim(), sender);
            } else {
                if (this.plugin.getPlayerManager().getMembershipChannel(sender).equals(globalChannel)) {
                    sender.sendMessage(ChannelsPlugin.get().getConfig().getString("global-chat.switch.no-change-msg", "Global chat is already your default channel."));
                } else {
                    sender.sendMessage(ChatColor.YELLOW + ChannelsPlugin.get().getConfig().getString("global-chat.switch.success-msg", "Changed default channel to global chat."));
                    this.plugin.getPlayerManager().setMembershipChannel(sender, globalChannel);
                }
            }
        } else {
            Channel channel = this.plugin.getPlayerManager().getMembershipChannel(sender);
            if (channel != null) {
                event.setCancelled(true);
                channel.sendMessage(message, sender);
            }
        }
    }
}
