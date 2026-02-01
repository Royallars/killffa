package com.killffa.killffa;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface KillffaNpcHandler {
    boolean createJoinNpc(Player player);

    boolean removeJoinNpc(CommandSender sender);
}
