package com.killffa.killffa;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillffaNpcManager implements Listener, KillffaNpcHandler {
    private static final String NPC_ID_PATH = "join-npc-id";
    private final KillffaPlugin plugin;

    public KillffaNpcManager(KillffaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean createJoinNpc(Player player) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
            player.sendMessage(ChatColor.RED + "Citizens is not installed.");
            return true;
        }
        Location location = player.getLocation();
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.DARK_RED + "Killffa Join");
        npc.spawn(location);
        plugin.getConfig().set(NPC_ID_PATH, npc.getId());
        plugin.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Join NPC created with id " + npc.getId() + ".");
        return true;
    }

    public boolean removeJoinNpc(CommandSender sender) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
            sender.sendMessage(ChatColor.RED + "Citizens is not installed.");
            return true;
        }
        int npcId = plugin.getConfig().getInt(NPC_ID_PATH, -1);
        if (npcId == -1) {
            sender.sendMessage(ChatColor.YELLOW + "No join NPC is configured.");
            return true;
        }
        NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc != null) {
            npc.destroy();
        }
        plugin.getConfig().set(NPC_ID_PATH, null);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Join NPC removed.");
        return true;
    }

    @EventHandler
    public void onNpcRightClick(NPCRightClickEvent event) {
        int npcId = plugin.getConfig().getInt(NPC_ID_PATH, -1);
        if (npcId == -1) {
            return;
        }
        if (event.getNPC().getId() != npcId) {
            return;
        }
        Player player = event.getClicker();
        player.performCommand("killffa join");
    }
}
