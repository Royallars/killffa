package com.killffa.killffa;

import org.bukkit.plugin.java.JavaPlugin;

public final class KillffaPlugin extends JavaPlugin {
    private final KillffaArena arena = new KillffaArena();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        arena.load(getConfig());
        KillffaCommand command = new KillffaCommand(this, arena);
        getCommand("killffa").setExecutor(command);
        getCommand("killffa").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new KillffaListener(arena), this);
        getLogger().info("Killffa enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Killffa disabled.");
    }

    public KillffaArena getArena() {
        return arena;
    }
}
