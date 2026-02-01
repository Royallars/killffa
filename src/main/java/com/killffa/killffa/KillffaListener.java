package com.killffa.killffa;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class KillffaListener implements Listener {
    private final KillffaPlugin plugin;
    private final KillffaArena arena;

    public KillffaListener(KillffaPlugin plugin, KillffaArena arena) {
        this.plugin = plugin;
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
        if (killer != null && arena.isParticipant(killer)) {
            arena.recordKill(killer);
            handleKillRewards(killer);
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
        arena.applySpawnProtection(player, plugin.getConfig().getInt("spawn-protection-seconds", 3));
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
        arena.applySpawnProtection(player, plugin.getConfig().getInt("spawn-protection-seconds", 3));
        player.sendMessage(ChatColor.GRAY + "You fell into the void, back to the fight!");
    }

    @EventHandler
    public void onSpawnProtectionDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!arena.isParticipant(player)) {
            return;
        }
        if (!arena.isSpawnProtected(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (arena.isParticipant(player)) {
            arena.removeParticipant(player);
        }
        arena.clearSpawnProtection(player);
    }

    @EventHandler
    public void onSandPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!arena.isParticipant(player)) {
            return;
        }
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.SAND) {
            return;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (block.getType() == Material.SAND) {
                block.setType(Material.AIR);
            }
        }, 100L);
    }

    private void handleKillRewards(Player killer) {
        double healHearts = plugin.getConfig().getDouble("kill-heal-hearts", 2.0);
        if (healHearts > 0) {
            double healAmount = healHearts * 2.0;
            killer.setHealth(Math.min(killer.getMaxHealth(), killer.getHealth() + healAmount));
            killer.setFoodLevel(Math.min(20, killer.getFoodLevel() + 4));
        }
        PlayerStats stats = arena.getStats(killer);
        int currentStreak = stats.getCurrentStreak();
        if (plugin.getConfig().getIntegerList("streak-announcements").contains(currentStreak)) {
            plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "Killffa" + ChatColor.GRAY + ": "
                + ChatColor.WHITE + killer.getName() + ChatColor.GRAY + " is on a "
                + ChatColor.RED + currentStreak + ChatColor.GRAY + " kill streak!");
            giveStreakReward(killer);
        }
    }

    private void giveStreakReward(Player killer) {
        String materialName = plugin.getConfig().getString("streak-reward-item", "GOLDEN_APPLE");
        Material material = Material.matchMaterial(materialName == null ? "GOLDEN_APPLE" : materialName);
        if (material == null || material == Material.AIR) {
            return;
        }
        int amount = plugin.getConfig().getInt("streak-reward-amount", 1);
        if (amount <= 0) {
            return;
        }
        killer.getInventory().addItem(new ItemStack(material, amount));
    }
}
