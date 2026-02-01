package com.killffa.killffa;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoopNpcManager implements KillffaNpcHandler {

    @Override
    public boolean createJoinNpc(Player player) {
        player.sendMessage(ChatColor.RED + "Citizens is not installed.");
        return true;
    }

    @Override
    public boolean removeJoinNpc(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Citizens is not installed.");
        return true;
    }
}
