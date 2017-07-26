package com.github.rmsy.channels.command;

import com.github.rmsy.channels.Channel;
import com.github.rmsy.channels.ChannelsPlugin;
import com.github.rmsy.channels.PlayerManager;
import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Commands for dealing with the global channel. */
public final class GlobalChannelCommands {

    @Command(
            aliases = {"g", "shout"},
            desc = "Sends a message to the global channel (or sets the global channel to your default channel).",
            max = -1,
            min = 0,
            anyFlags = true,
            usage = "[message...]"
    )
    @CommandPermissions({ChannelsPlugin.GLOBAL_CHANNEL_RECEIVE_NODE, ChannelsPlugin.GLOBAL_CHANNEL_SEND_NODE})
    @Console
    public static void globalChannelCommand(final CommandContext arguments, final CommandSender sender) throws CommandException {
        if (Preconditions.checkNotNull(arguments, "arguments").argsLength() == 0) {
            if (Preconditions.checkNotNull(sender, "sender").hasPermission(ChannelsPlugin.GLOBAL_CHANNEL_SEND_NODE)) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    ChannelsPlugin plugin = ChannelsPlugin.get();
                    PlayerManager playerManager = plugin.getPlayerManager();
                    Channel oldChannel = playerManager.getMembershipChannel(player);
                    Channel globalChannel = plugin.getGlobalChannel();
                    playerManager.setMembershipChannel(player, globalChannel);
                    if (!oldChannel.equals(globalChannel)) {
                        sender.sendMessage(ChatColor.YELLOW + ChannelsPlugin.get().getConfig().getString("global-chat.switch.success-msg", "Changed default channel to global chat."));
                    } else {
                        throw new CommandException(ChannelsPlugin.get().getConfig().getString("global-chat.switch.no-change-msg", "Global chat is already your default channel."));
                    }
                } else {
                    throw new CommandUsageException("You must provide a message.", "/g <message...>");
                }
            } else {
                throw new CommandPermissionsException();
            }
        } else if (Preconditions.checkNotNull(sender, "sender").hasPermission(ChannelsPlugin.GLOBAL_CHANNEL_SEND_NODE)) {
            Player sendingPlayer = null;
            if (sender instanceof Player) {
                sendingPlayer = (Player) sender;
            }
            ChannelsPlugin.get().getGlobalChannel().sendMessage(arguments.getJoinedStrings(0), sendingPlayer);
            if (!sender.hasPermission(ChannelsPlugin.GLOBAL_CHANNEL_RECEIVE_NODE)) {
                sender.sendMessage(ChatColor.YELLOW + ChannelsPlugin.get().getConfig().getString("global-chat.message-success-msg", "Message sent."));
            }
        } else {
            throw new CommandPermissionsException();
        }
    }
}
