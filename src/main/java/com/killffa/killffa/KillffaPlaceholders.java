package com.killffa.killffa;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class KillffaPlaceholders extends PlaceholderExpansion {
    private final KillffaPlugin plugin;
    private final KillffaArena arena;

    public KillffaPlaceholders(KillffaPlugin plugin, KillffaArena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public String getIdentifier() {
        return "killffa";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "0";
        }
        if (params.equalsIgnoreCase("kills")) {
            return String.valueOf(arena.getStats(player.getUniqueId()).getKills());
        }
        if (params.equalsIgnoreCase("deaths")) {
            return String.valueOf(arena.getStats(player.getUniqueId()).getDeaths());
        }
        if (params.equalsIgnoreCase("kdr")) {
            PlayerStats stats = arena.getStats(player.getUniqueId());
            int deaths = stats.getDeaths();
            double kdr = deaths == 0 ? stats.getKills() : (double) stats.getKills() / deaths;
            return String.format("%.2f", kdr);
        }
        if (params.equalsIgnoreCase("streak")) {
            return String.valueOf(arena.getStats(player.getUniqueId()).getCurrentStreak());
        }
        if (params.equalsIgnoreCase("best_streak")) {
            return String.valueOf(arena.getStats(player.getUniqueId()).getBestStreak());
        }
        return null;
    }
}
