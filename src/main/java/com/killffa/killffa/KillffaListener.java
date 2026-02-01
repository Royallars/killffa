package com.killffa.killffa;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class KillffaListener implements Listener {
    private final KillffaArena arena;

    public KillffaListener(KillffaArena arena) {
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!arena.isParticipant(victim)) {
            return;
        }
        event.setKeepInventory(true);
        event.getDrops().clear();
        event.setDeathMessage(null);
        arena.recordDeath(victim);
        Player killer = victim.getKiller();
        if (killer != null) {
            arena.recordKill(killer);
            killer.sendMessage(ChatColor.DARK_RED + "Killffa" + ChatColor.GRAY + ": You eliminated " + ChatColor.WHITE + victim.getName());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!arena.isParticipant(player)) {
            return;
        }
        Location spawn = arena.getSpawn();
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
        arena.giveKit(player);
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!arena.isParticipant(player)) {
            return;
        }
        event.setCancelled(true);
        arena.recordDeath(player);
        Location spawn = arena.getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
        arena.giveKit(player);
        player.sendMessage(ChatColor.GRAY + "You fell into the void, back to the fight!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (arena.isParticipant(player)) {
            arena.removeParticipant(player);
        }
    }
}
