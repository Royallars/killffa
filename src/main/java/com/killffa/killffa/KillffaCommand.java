package com.killffa.killffa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class KillffaCommand implements CommandExecutor, TabCompleter {
    private final KillffaPlugin plugin;
    private final KillffaArena arena;

    public KillffaCommand(KillffaPlugin plugin, KillffaArena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);
        switch (subcommand) {
            case "info":
                sender.sendMessage(ChatColor.DARK_RED + "Killffa" + ChatColor.GRAY + " v" + plugin.getDescription().getVersion());
                sender.sendMessage(ChatColor.GRAY + "Use /killffa join to enter the free-for-all arena.");
                return true;
            case "join":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can join the arena.");
                    return true;
                }
                return handleJoin((Player) sender);
            case "leave":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can leave the arena.");
                    return true;
                }
                return handleLeave((Player) sender);
            case "setspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can set the spawn.");
                    return true;
                }
                if (!sender.hasPermission("killffa.admin")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                return handleSetSpawn((Player) sender);
            case "slay":
                if (!sender.hasPermission("killffa.admin")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                return handleSlay(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleJoin(Player player) {
        if (!player.hasPermission("killffa.join")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to join Killffa.");
            return true;
        }
        if (!arena.hasSpawn()) {
            player.sendMessage(ChatColor.RED + "Killffa spawn is not set yet.");
            return true;
        }
        if (!arena.addParticipant(player)) {
            player.sendMessage(ChatColor.YELLOW + "You are already in the Killffa arena.");
            return true;
        }
        player.sendMessage(ChatColor.DARK_RED + "Killffa" + ChatColor.GRAY + ": You joined the arena!");
        teleportAndKit(player, arena.getSpawn());
        return true;
    }

    private boolean handleLeave(Player player) {
        if (!arena.removeParticipant(player)) {
            player.sendMessage(ChatColor.YELLOW + "You are not in the Killffa arena.");
            return true;
        }
        player.sendMessage(ChatColor.GRAY + "You left the Killffa arena.");
        player.getInventory().clear();
        return true;
    }

    private boolean handleSetSpawn(Player player) {
        Location location = player.getLocation();
        arena.setSpawn(location);
        arena.save(plugin.getConfig());
        plugin.saveConfig();
        player.sendMessage(ChatColor.GREEN + "Killffa spawn set to your current location.");
        return true;
    }

    private boolean handleSlay(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /killffa slay <player>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }
        target.setHealth(0.0);
        sender.sendMessage(ChatColor.DARK_RED + "Target eliminated: " + ChatColor.WHITE + target.getName());
        return true;
    }

    private void teleportAndKit(Player player, Location spawn) {
        player.teleport(spawn);
        arena.giveKit(player);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Killffa commands:");
        sender.sendMessage(ChatColor.GRAY + "/killffa info" + ChatColor.DARK_GRAY + " - plugin info");
        sender.sendMessage(ChatColor.GRAY + "/killffa join" + ChatColor.DARK_GRAY + " - join the arena");
        sender.sendMessage(ChatColor.GRAY + "/killffa leave" + ChatColor.DARK_GRAY + " - leave the arena");
        if (sender.hasPermission("killffa.admin")) {
            sender.sendMessage(ChatColor.GRAY + "/killffa setspawn" + ChatColor.DARK_GRAY + " - set arena spawn");
            sender.sendMessage(ChatColor.GRAY + "/killffa slay <player>" + ChatColor.DARK_GRAY + " - eliminate a player");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("info");
            completions.add("join");
            completions.add("leave");
            if (sender.hasPermission("killffa.admin")) {
                completions.add("setspawn");
                completions.add("slay");
            }
            return completions;
        }
        if (args.length == 2 && "slay".equalsIgnoreCase(args[0])) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
