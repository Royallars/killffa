package com.killffa.killffa;

import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

public class KillffaArena {
    private static final String SPAWN_PATH = "spawn";
    private static final String SPAWN_WORLD = SPAWN_PATH + ".world";
    private static final String SPAWN_X = SPAWN_PATH + ".x";
    private static final String SPAWN_Y = SPAWN_PATH + ".y";
    private static final String SPAWN_Z = SPAWN_PATH + ".z";
    private static final String SPAWN_YAW = SPAWN_PATH + ".yaw";
    private static final String SPAWN_PITCH = SPAWN_PATH + ".pitch";
    private static final String STATS_PATH = "stats";

    private final Set<UUID> participants = new HashSet<>();
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private final Map<UUID, Long> spawnProtection = new HashMap<>();
    private Location spawn;
    private int maxPlayers = 8;

    public void load(FileConfiguration config) {
        if (!config.contains(SPAWN_WORLD)) {
            maxPlayers = config.getInt("max-players", maxPlayers);
            loadStats(config);
            return;
        }
        World world = Bukkit.getWorld(config.getString(SPAWN_WORLD));
        if (world == null) {
            maxPlayers = config.getInt("max-players", maxPlayers);
            loadStats(config);
            return;
        }
        double x = config.getDouble(SPAWN_X);
        double y = config.getDouble(SPAWN_Y);
        double z = config.getDouble(SPAWN_Z);
        float yaw = (float) config.getDouble(SPAWN_YAW);
        float pitch = (float) config.getDouble(SPAWN_PITCH);
        spawn = new Location(world, x, y, z, yaw, pitch);
        maxPlayers = config.getInt("max-players", maxPlayers);
        loadStats(config);
    }

    public void save(FileConfiguration config) {
        if (spawn == null) {
            config.set("max-players", maxPlayers);
            saveStats(config);
            return;
        }
        config.set(SPAWN_WORLD, spawn.getWorld().getName());
        config.set(SPAWN_X, spawn.getX());
        config.set(SPAWN_Y, spawn.getY());
        config.set(SPAWN_Z, spawn.getZ());
        config.set(SPAWN_YAW, spawn.getYaw());
        config.set(SPAWN_PITCH, spawn.getPitch());
        config.set("max-players", maxPlayers);
        saveStats(config);
    }

    public boolean hasSpawn() {
        return spawn != null;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = Math.max(1, maxPlayers);
    }

    public boolean isParticipant(Player player) {
        return participants.contains(player.getUniqueId());
    }

    public boolean addParticipant(Player player) {
        if (participants.size() >= maxPlayers) {
            return false;
        }
        stats.putIfAbsent(player.getUniqueId(), new PlayerStats());
        return participants.add(player.getUniqueId());
    }

    public boolean removeParticipant(Player player) {
        spawnProtection.remove(player.getUniqueId());
        return participants.remove(player.getUniqueId());
    }

    public void recordKill(Player killer) {
        stats.computeIfAbsent(killer.getUniqueId(), ignored -> new PlayerStats()).incrementKills();
    }

    public void recordDeath(Player victim) {
        stats.computeIfAbsent(victim.getUniqueId(), ignored -> new PlayerStats()).incrementDeaths();
    }

    public PlayerStats getStats(Player player) {
        return getStats(player.getUniqueId());
    }

    public PlayerStats getStats(UUID playerId) {
        return stats.getOrDefault(playerId, new PlayerStats());
    }

    public void resetStats(Player player) {
        stats.put(player.getUniqueId(), new PlayerStats());
    }

    public void loadStats(FileConfiguration config) {
        stats.clear();
        ConfigurationSection section = config.getConfigurationSection(STATS_PATH);
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                int kills = section.getInt(key + ".kills");
                int deaths = section.getInt(key + ".deaths");
                int currentStreak = section.getInt(key + ".current-streak");
                int bestStreak = section.getInt(key + ".best-streak");
                stats.put(playerId, new PlayerStats(kills, deaths, currentStreak, bestStreak));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed UUIDs.
            }
        }
    }

    public void saveStats(FileConfiguration config) {
        config.set(STATS_PATH, null);
        ConfigurationSection section = config.createSection(STATS_PATH);
        for (Map.Entry<UUID, PlayerStats> entry : stats.entrySet()) {
            String key = entry.getKey().toString();
            PlayerStats playerStats = entry.getValue();
            section.set(key + ".kills", playerStats.getKills());
            section.set(key + ".deaths", playerStats.getDeaths());
            section.set(key + ".current-streak", playerStats.getCurrentStreak());
            section.set(key + ".best-streak", playerStats.getBestStreak());
        }
    }

    public void applySpawnProtection(Player player, int seconds) {
        if (seconds <= 0) {
            return;
        }
        spawnProtection.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean isSpawnProtected(Player player) {
        Long expiresAt = spawnProtection.get(player.getUniqueId());
        if (expiresAt == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiresAt) {
            spawnProtection.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public void clearSpawnProtection(Player player) {
        spawnProtection.remove(player.getUniqueId());
    }

    public List<Map.Entry<UUID, PlayerStats>> getTopKills(int limit) {
        return stats.entrySet().stream()
            .sorted(Comparator.comparingInt(entry -> -entry.getValue().getKills()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public void giveKit(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, new ItemStack(Material.IRON_SWORD));
        inventory.setItem(1, new ItemStack(Material.COOKED_BEEF, 16));
        inventory.setArmorContents(new ItemStack[] {
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_HELMET)
        });
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
    }
}
