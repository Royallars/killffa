package com.killffa.killffa;

import org.bukkit.plugin.java.JavaPlugin;

public final class KillffaPlugin extends JavaPlugin {
    private final KillffaArena arena = new KillffaArena();
    private KillffaMenu menu;
    private KillffaNpcHandler npcManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        arena.load(getConfig());
        menu = new KillffaMenu(arena);
        if (getServer().getPluginManager().isPluginEnabled("Citizens")) {
            npcManager = new KillffaNpcManager(this);
            getServer().getPluginManager().registerEvents((KillffaNpcManager) npcManager, this);
        } else {
            npcManager = new NoopNpcManager();
        }
        KillffaCommand command = new KillffaCommand(this, arena, menu, npcManager);
        getCommand("killffa").setExecutor(command);
        getCommand("killffa").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new KillffaListener(this, arena), this);
        getServer().getPluginManager().registerEvents(menu, this);
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new KillffaPlaceholders(this, arena).register();
        }
        getLogger().info("Killffa enabled.");
    }

    @Override
    public void onDisable() {
        arena.save(getConfig());
        saveConfig();
        getLogger().info("Killffa disabled.");
    }

    public KillffaArena getArena() {
        return arena;
    }
}
