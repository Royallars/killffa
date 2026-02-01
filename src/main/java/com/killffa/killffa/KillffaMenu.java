package com.killffa.killffa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KillffaMenu implements Listener {
    private static final Component MENU_TITLE = Component.text("Killffa Arena", NamedTextColor.DARK_RED);
    private final KillffaArena arena;

    public KillffaMenu(KillffaArena arena) {
        this.arena = arena;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 27, MENU_TITLE);
        inventory.setItem(11, buildItem(Material.IRON_SWORD, ChatColor.GREEN + "Join Arena", List.of(ChatColor.GRAY + "Jump into the fight!")));
        inventory.setItem(13, buildStatsItem(player));
        inventory.setItem(15, buildLeaderboardItem());
        if (player.hasPermission("killffa.admin")) {
            inventory.setItem(22, buildItem(Material.REDSTONE, ChatColor.YELLOW + "Set Spawn", List.of(ChatColor.GRAY + "Set arena spawn here")));
        }
        player.openInventory(inventory);
    }

    private ItemStack buildStatsItem(Player player) {
        PlayerStats stats = arena.getStats(player);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Kills: " + ChatColor.WHITE + stats.getKills());
        lore.add(ChatColor.GRAY + "Deaths: " + ChatColor.WHITE + stats.getDeaths());
        lore.add(ChatColor.GRAY + "Streak: " + ChatColor.WHITE + stats.getCurrentStreak());
        lore.add(ChatColor.GRAY + "Best streak: " + ChatColor.WHITE + stats.getBestStreak());
        lore.add(ChatColor.DARK_GRAY + "Click to refresh.");
        return buildItem(Material.PAPER, ChatColor.AQUA + "Your Stats", lore);
    }

    private ItemStack buildLeaderboardItem() {
        List<String> lore = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<UUID, PlayerStats> entry : arena.getTopKills(5)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String name = offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName();
            lore.add(ChatColor.GRAY + "#" + rank + " " + ChatColor.WHITE + name
                + ChatColor.DARK_GRAY + " - " + ChatColor.RED + entry.getValue().getKills());
            rank++;
        }
        if (lore.isEmpty()) {
            lore.add(ChatColor.GRAY + "No kills yet.");
        }
        lore.add(ChatColor.DARK_GRAY + "Use ajLeaderboards:");
        lore.add(ChatColor.DARK_GRAY + "%killffa_kills% / %killffa_deaths%");
        return buildItem(Material.NETHER_STAR, ChatColor.LIGHT_PURPLE + "Leaderboard", lore);
    }

    private ItemStack buildItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!event.getView().title().equals(MENU_TITLE)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        switch (clicked.getType()) {
            case IRON_SWORD:
                player.closeInventory();
                player.performCommand("killffa join");
                break;
            case PAPER:
                open(player);
                break;
            case NETHER_STAR:
                open(player);
                break;
            case REDSTONE:
                if (player.hasPermission("killffa.admin")) {
                    player.performCommand("killffa setspawn");
                    player.closeInventory();
                }
                break;
            default:
                break;
        }
    }
}
