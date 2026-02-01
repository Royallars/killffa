package com.killffa.killffa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

    private final Set<UUID> participants = new HashSet<>();
    private Location spawn;

    public void load(FileConfiguration config) {
        if (!config.contains(SPAWN_WORLD)) {
            return;
        }
        World world = Bukkit.getWorld(config.getString(SPAWN_WORLD));
        if (world == null) {
            return;
        }
        double x = config.getDouble(SPAWN_X);
        double y = config.getDouble(SPAWN_Y);
        double z = config.getDouble(SPAWN_Z);
        float yaw = (float) config.getDouble(SPAWN_YAW);
        float pitch = (float) config.getDouble(SPAWN_PITCH);
        spawn = new Location(world, x, y, z, yaw, pitch);
    }

    public void save(FileConfiguration config) {
        if (spawn == null) {
            return;
        }
        config.set(SPAWN_WORLD, spawn.getWorld().getName());
        config.set(SPAWN_X, spawn.getX());
        config.set(SPAWN_Y, spawn.getY());
        config.set(SPAWN_Z, spawn.getZ());
        config.set(SPAWN_YAW, spawn.getYaw());
        config.set(SPAWN_PITCH, spawn.getPitch());
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

    public boolean isParticipant(Player player) {
        return participants.contains(player.getUniqueId());
    }

    public boolean addParticipant(Player player) {
        return participants.add(player.getUniqueId());
    }

    public boolean removeParticipant(Player player) {
        return participants.remove(player.getUniqueId());
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
