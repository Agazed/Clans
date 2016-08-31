package net.agazed.clans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Clans extends JavaPlugin implements Listener {

    @SuppressWarnings("rawtypes")
    List inChat = new ArrayList();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command can only be run in-game!");
        }
        Player player = (Player) sender;

        // Clan Help

        if (cmd.getName().equalsIgnoreCase("clan")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                player.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Anyone" + ChatColor.WHITE
                        + " -----");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "help " + ChatColor.WHITE
                        + "- Displays this page");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "info [player|clan] "
                        + ChatColor.WHITE + "- View clan information");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "create <clan> <password> "
                        + ChatColor.WHITE + "- Create a clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "join <clan> <password> "
                        + ChatColor.WHITE + "- Join a clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "leave " + ChatColor.WHITE
                        + "- Leave your current clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "chat [message] "
                        + ChatColor.WHITE + "- Send a message to your clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "hq " + ChatColor.WHITE
                        + "- Teleport to clan HQ point");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "rally " + ChatColor.WHITE
                        + "- Teleport to clan rally point");
                player.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Clan Managers" + ChatColor.WHITE
                        + " -----");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "password <password> "
                        + ChatColor.WHITE + "- Change your clan password");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "kick <player> " + ChatColor.WHITE
                        + "- Kick a player from your clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "promote <player> "
                        + ChatColor.WHITE + "- Promote a player in your clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "demote <player> "
                        + ChatColor.WHITE + "- Demote a player in your clan");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "sethq " + ChatColor.WHITE
                        + "- Set the clan HQ point");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "setrally " + ChatColor.WHITE
                        + "- Set the clan rally point");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "ff " + ChatColor.WHITE
                        + "- Toggle clan friendly fire");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "tag <name> " + ChatColor.WHITE
                        + "- Change your clan name");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "desc <desc> " + ChatColor.WHITE
                        + "- Edit the clan's description");
                player.sendMessage(ChatColor.DARK_AQUA + "/clan " + ChatColor.GRAY + "disband " + ChatColor.WHITE
                        + "- Disband your clan");
                return true;
            }

            // Clan Create

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 1 || args.length == 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan create <clan> <password>");
                    return true;
                }
                if (getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan") != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a clan!");
                    return true;
                }
                if (getConfig().getString("clans" + "." + args[1]) != null) {
                    player.sendMessage(ChatColor.RED + "Clan tag is already taken!");
                    return true;
                }
                getConfig().set("clans" + "." + args[1], args[1]);
                getConfig().set("clans" + "." + args[1] + ".password", args[2]);
                List memberlist = getConfig().getStringList("clans" + "." + args[1] + ".members");
                List promotedlist = getConfig().getStringList("clans" + "." + args[1] + ".promoted");
                memberlist.add(player.getUniqueId().toString());
                promotedlist.add(player.getUniqueId().toString());
                getConfig().set("clans" + "." + args[1] + ".ff", false);
                getConfig().set("clans" + "." + args[1] + ".members", memberlist);
                getConfig().set("clans" + "." + args[1] + ".promoted", promotedlist);
                getConfig().set("players" + "." + player.getUniqueId().toString() + ".clan", args[1]);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You successfully created clan " + args[1]);
                for (Player server : Bukkit.getOnlinePlayers()) {
                    server.sendMessage(ChatColor.GRAY + player.getName() + " created clan " + args[1]);
                }
                return true;
            }

            // Clan Disband

            if (args[0].equalsIgnoreCase("disband")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                getConfig().set("clans" + "." + clan, null);
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(membersuuid.toString());
                    getConfig().set("players" + "." + members.getName() + ".clan", null);
                }
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You successfully disbanded clan " + clan);
                for (Player server : Bukkit.getOnlinePlayers()) {
                    server.sendMessage(ChatColor.GRAY + player.getName() + " disbanded clan " + clan);
                }
                return true;
            }

            // Clan Info

            if (args[0].equalsIgnoreCase("info")) {
                if (args.length == 1) {
                    String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                    if (clan == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a clan!");
                        return true;
                    }
                    player.sendMessage("-------- " + ChatColor.DARK_AQUA + ChatColor.BOLD + clan + ChatColor.WHITE
                            + " --------");
                    String desc = (getConfig().getString("clans" + "." + clan + ".desc") == null) ? "Not set"
                            : getConfig().getString("clans" + "." + clan + ".desc");
                    player.sendMessage(ChatColor.GRAY + "Description: " + desc);
                    player.sendMessage(ChatColor.GRAY + "Password: "
                            + getConfig().getString("clans" + "." + clan + ".password"));
                    String ff = (getConfig().getBoolean("clans" + "." + clan + ".ff")) ? "on" : "off";
                    player.sendMessage(ChatColor.GRAY + "Friendly fire is " + ff);
                    String hq = (getConfig().getString("clans" + "." + clan + ".hq") == null) ? "not set" : "set";
                    player.sendMessage(ChatColor.GRAY + "HQ point is " + hq);
                    String rally = (getConfig().getString("clans" + "." + clan + ".rally") == null) ? "not set" : "set";
                    player.sendMessage(ChatColor.GRAY + "Rally point is " + rally);
                    player.sendMessage(ChatColor.GRAY + "Members: ");
                    List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                    List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                    for (Object membersuuid : memberlist) {
                        OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                        ChatColor isPromoted = (promotedlist.contains(members.getUniqueId().toString())) ? ChatColor.DARK_AQUA
                                : ChatColor.GRAY;
                        String isOnline = (members.isOnline()) ? "Health: " + members.getPlayer().getHealth() / 20.0D
                                * 100.0D + "%" : "Offline";
                        player.sendMessage(isPromoted + " " + members.getName() + ChatColor.GRAY + " - " + isOnline);
                    }
                    return true;
                }
                OfflinePlayer target = getServer().getPlayerExact(args[1]);
                if (target == null) {
                    String clan = getConfig().getString("clans" + "." + args[1]);
                    if (clan == null) {
                        player.sendMessage(ChatColor.RED + "Player or clan does not exist!");
                        return true;
                    }
                    player.sendMessage("-------- " + ChatColor.DARK_AQUA + ChatColor.BOLD + args[1] + ChatColor.WHITE
                            + " --------");
                    String desc = (getConfig().getString("clans" + "." + args[1] + ".desc") == null) ? "Not set"
                            : getConfig().getString("clans" + "." + args[1] + ".desc");
                    player.sendMessage(ChatColor.GRAY + "Description: " + desc);
                    player.sendMessage(ChatColor.GRAY + "Members: ");
                    List memberlist = getConfig().getStringList("clans" + "." + args[1] + ".members");
                    List promotedlist = getConfig().getStringList("clans" + "." + args[1] + ".promoted");
                    for (Object membersuuid : memberlist) {
                        OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                        ChatColor isPromoted = (promotedlist.contains(members.getUniqueId().toString())) ? ChatColor.DARK_AQUA
                                : ChatColor.GRAY;
                        String isOnline = (members.isOnline()) ? "Online" : "Offline";
                        player.sendMessage(isPromoted + " " + members.getName() + ChatColor.GRAY + " - " + isOnline);
                    }
                    return true;
                }
                String clan = getConfig().getString("players" + "." + target.getUniqueId() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "Player does not have a clan!");
                    return true;
                }
                player.sendMessage("-------- " + ChatColor.DARK_AQUA + ChatColor.BOLD + clan + ChatColor.WHITE
                        + " --------");
                String desc = (getConfig().getString("clans" + "." + clan + ".desc") == null) ? "Not set" : getConfig()
                        .getString("clans" + "." + clan + ".desc");
                player.sendMessage(ChatColor.GRAY + "Description: " + desc);
                player.sendMessage(ChatColor.GRAY + "Members: ");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    ChatColor isPromoted = (promotedlist.contains(members.getUniqueId().toString())) ? ChatColor.DARK_AQUA
                            : ChatColor.GRAY;
                    String isOnline = (members.isOnline()) ? "Online" : "Offline";
                    player.sendMessage(isPromoted + " " + members.getName() + ChatColor.GRAY + " - " + isOnline);
                }
                return true;
            }

            // Clan Join

            if (args[0].equalsIgnoreCase("join")) {
                if (args.length == 1 || args.length == 2) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan join <clan> <password>");
                    return true;
                }
                if (getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan") != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a clan!");
                    return true;
                }
                if (getConfig().getString("clans" + "." + args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "Clan does not exist!");
                    return true;
                }
                String password = getConfig().getString("clans" + "." + args[1] + ".password");
                if (!args[2].equals(password)) {
                    player.sendMessage(ChatColor.RED + "Incorrect password!");
                    return true;
                }
                List memberlist = getConfig().getStringList("clans" + "." + args[1] + ".members");
                memberlist.add(player.getUniqueId().toString());
                getConfig().set("players" + "." + player.getUniqueId().toString() + ".clan", args[1]);
                getConfig().set("clans" + "." + args[1] + ".members", memberlist);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You joined clan " + args[1]);
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.GRAY + player.getName() + " joined the clan");
                    }
                }
                return true;
            }

            // Clan Leave

            if (args[0].equalsIgnoreCase("leave")) {
                if (getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan") == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a clan!");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (memberlist.size() == 1 || promotedlist.size() == 0) {
                    getConfig().set("clans" + "." + clan, null);
                    for (Object membersuuid : memberlist) {
                        OfflinePlayer members = getServer().getOfflinePlayer(membersuuid.toString());
                        getConfig().set("players" + "." + members.getName() + ".clan", null);
                    }
                    saveConfig();
                    player.sendMessage(ChatColor.GRAY + "You left clan " + clan);
                    for (Player server : Bukkit.getOnlinePlayers()) {
                        server.sendMessage(ChatColor.GRAY + player.getName() + " disbanded clan " + clan);
                    }
                    return true;
                }
                if (promotedlist.contains(player.getUniqueId().toString())) {
                    promotedlist.remove(player.getUniqueId().toString());
                }
                memberlist.remove(player.getUniqueId().toString());
                getConfig().set("players" + "." + player.getUniqueId().toString() + ".clan", null);
                getConfig().set("clans" + "." + clan + ".members", memberlist);
                getConfig().set("clans" + "." + clan + ".promoted", promotedlist);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You left clan " + clan);
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.GRAY + player.getName() + " left the clan");
                    }
                }
                return true;
            }

            // Clan Kick

            if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan kick <player>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                OfflinePlayer target = getServer().getOfflinePlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player does not exist!");
                    return true;
                }
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                if (!memberlist.contains(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "Player is not in your clan!");
                    return true;
                }
                if (player.getUniqueId().toString().equals(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You cannot kick yourself!");
                    return true;
                }
                if (promotedlist.contains(target.getUniqueId().toString())) {
                    promotedlist.remove(target.getUniqueId().toString());
                }
                memberlist.remove(target.getUniqueId().toString());
                getConfig().set("players" + "." + target.getUniqueId().toString() + ".clan", null);
                getConfig().set("clans" + "." + clan + ".members", memberlist);
                getConfig().set("clans" + "." + clan + ".promoted", promotedlist);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "Kicked player " + target.getName());
                target.getPlayer().sendMessage(ChatColor.GRAY + "You were kicked from the clan");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.GRAY + target.getPlayer().getName()
                                + " was kicked from the clan");
                    }
                }
                return true;
            }

            // Clan Password

            if (args[0].equalsIgnoreCase("password")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan password <password>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                getConfig().set("clans" + "." + clan + ".password", args[1]);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "Successfully changed password to " + args[1]);
                return true;
            }

            // Clan Promote

            if (args[0].equalsIgnoreCase("promote")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan promote <player>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                OfflinePlayer target = getServer().getOfflinePlayer(args[1]);
                if (!memberlist.contains(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "Player is not in your clan!");
                    return true;
                }
                if (promotedlist.contains(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "Player is already promoted!");
                    return true;
                }
                promotedlist.add(target.getUniqueId().toString());
                getConfig().set("clans" + "." + clan + ".promoted", promotedlist);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You promoted " + target.getPlayer().getName());
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.GRAY + target.getPlayer().getName() + " has been promoted");
                    }
                }
                return true;
            }

            // Clan Demote

            if (args[0].equalsIgnoreCase("demote")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan demote <player>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                OfflinePlayer target = getServer().getOfflinePlayer(args[1]);
                if (!promotedlist.contains(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "Player is not promoted!");
                    return true;
                }
                if (player.getUniqueId().toString().equals(target.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You cannot demote yourself!");
                    return true;
                }
                promotedlist.remove(target.getUniqueId().toString());
                getConfig().set("clans" + "." + clan + ".promoted", promotedlist);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You demoted " + target.getPlayer().getName());
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.GRAY + target.getPlayer().getName() + " has been demoted");
                    }
                }
                return true;
            }

            // Clan Chat

            if (args[0].equalsIgnoreCase("chat")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                if (args.length == 1) {
                    if (inChat.contains(player.getName())) {
                        inChat.remove(player.getName());
                        player.sendMessage(ChatColor.GRAY + "You have entered: " + ChatColor.WHITE + "Public Chat");
                        return true;
                    }
                    inChat.add(player.getName());
                    player.sendMessage(ChatColor.GRAY + "You have entered: " + ChatColor.DARK_AQUA + "Team Chat");
                    return true;
                }
                String message = "";
                for (int words = 1; words < args.length; words++) {
                    message = message + args[words] + " ";
                }
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Clan"
                                + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + player.getName() + ChatColor.GRAY
                                + ": " + ChatColor.DARK_AQUA + message);
                    }
                }
                return true;
            }

            // Clan HQ

            if (args[0].equalsIgnoreCase("hq")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                String hq = getConfig().getString("clans" + "." + clan + ".hq");
                if (hq == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a HQ point set!");
                    return true;
                }
                for (Entity e : player.getNearbyEntities(30.0D, 127.0D, 30.0D)) {
                    if (e instanceof Player) {
                        Player nearby = (Player) e;
                        List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                        if (!memberlist.contains(nearby.getUniqueId().toString())) {
                            player.sendMessage(ChatColor.RED + "You cannot teleport with players nearby!");
                            return true;
                        }
                    }
                }
                World world = getServer().getWorld(getConfig().get("clans" + "." + clan + ".hq" + ".world").toString());
                double x = getConfig().getDouble("clans" + "." + clan + ".hq" + ".x");
                double y = getConfig().getDouble("clans" + "." + clan + ".hq" + ".y");
                double z = getConfig().getDouble("clans" + "." + clan + ".hq" + ".z");
                float yaw = (float) getConfig().getDouble("clans" + "." + clan + ".hq" + ".yaw");
                float pitch = (float) getConfig().getDouble("clans" + "." + clan + ".hq" + ".pitch");
                Location hqpoint = new Location(world, x, y, z, yaw, pitch);
                player.teleport(hqpoint);
                player.sendMessage(ChatColor.GRAY + "Teleported to clan HQ");
                return true;
            }

            // Clan Rally

            if (args[0].equalsIgnoreCase("rally")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                String rally = getConfig().getString("clans" + "." + clan + ".rally");
                if (rally == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a rally point set!");
                    return true;
                }
                for (Entity e : player.getNearbyEntities(30.0D, 127.0D, 30.0D)) {
                    if (e instanceof Player) {
                        Player nearby = (Player) e;
                        List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                        if (!memberlist.contains(nearby.getUniqueId().toString())) {
                            player.sendMessage(ChatColor.RED + "You cannot teleport with players nearby!");
                            return true;
                        }
                    }
                }
                World world = getServer().getWorld(
                        getConfig().get("clans" + "." + clan + ".rally" + ".world").toString());
                double x = getConfig().getDouble("clans" + "." + clan + ".rally" + ".x");
                double y = getConfig().getDouble("clans" + "." + clan + ".rally" + ".y");
                double z = getConfig().getDouble("clans" + "." + clan + ".rally" + ".z");
                float yaw = (float) getConfig().getDouble("clans" + "." + clan + ".rally" + ".yaw");
                float pitch = (float) getConfig().getDouble("clans" + "." + clan + ".rally" + ".pitch");
                Location rallypoint = new Location(world, x, y, z, yaw, pitch);
                player.teleport(rallypoint);
                player.sendMessage(ChatColor.GRAY + "Teleported to clan rally");
                return true;
            }

            // Clan SetHQ

            if (args[0].equalsIgnoreCase("sethq")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                getConfig().set("clans" + "." + clan + ".hq" + ".world", player.getLocation().getWorld().getName());
                getConfig().set("clans" + "." + clan + ".hq" + ".x", player.getLocation().getX());
                getConfig().set("clans" + "." + clan + ".hq" + ".y", player.getLocation().getY());
                getConfig().set("clans" + "." + clan + ".hq" + ".z", player.getLocation().getZ());
                getConfig().set("clans" + "." + clan + ".hq" + ".yaw", player.getLocation().getYaw());
                getConfig().set("clans" + "." + clan + ".hq" + ".pitch", player.getLocation().getPitch());
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "Hq set");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_AQUA + player.getName()
                                + " has updated the clan's HQ point!");
                    }
                }
                return true;
            }

            // Clan SetRally

            if (args[0].equalsIgnoreCase("setrally")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                getConfig().set("clans" + "." + clan + ".rally" + ".world", player.getLocation().getWorld().getName());
                getConfig().set("clans" + "." + clan + ".rally" + ".x", player.getLocation().getX());
                getConfig().set("clans" + "." + clan + ".rally" + ".y", player.getLocation().getY());
                getConfig().set("clans" + "." + clan + ".rally" + ".z", player.getLocation().getZ());
                getConfig().set("clans" + "." + clan + ".rally" + ".yaw", player.getLocation().getYaw());
                getConfig().set("clans" + "." + clan + ".rally" + ".pitch", player.getLocation().getPitch());
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "Rally set");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_AQUA + player.getName()
                                + " has updated the clan's rally point!");
                    }
                }
                return true;
            }

            // Clan FF

            if (args[0].equalsIgnoreCase("ff")) {
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                boolean ff = getConfig().getBoolean("clans" + "." + clan + ".ff");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                if (ff == false) {
                    getConfig().set("clans" + "." + clan + ".ff", true);
                    for (Object membersuuid : memberlist) {
                        OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                        if (members.isOnline()) {
                            Player onlinemembers = (Player) members;
                            onlinemembers.sendMessage(ChatColor.DARK_AQUA + "Clan friendly fire enabled");
                        }
                    }
                    return true;
                }
                getConfig().set("clans" + "." + clan + ".ff", false);
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_AQUA + "Clan friendly fire disabled");
                    }
                }
                return true;
            }

            // Clan Tag

            if (args[0].equalsIgnoreCase("tag")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan tag <tag>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(membersuuid.toString());
                    getConfig().set("players" + "." + members.getName() + ".clan", args[1]);
                }
                getConfig().set("clans" + "." + clan + ".", args[1]);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You successfully changed the clan tag to " + args[1]);
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_AQUA + player.getName() + " changed clan tag to "
                                + args[1]);
                    }
                }
                return true;
            }

            // Clan Desc

            if (args[0].equalsIgnoreCase("desc")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Correct usage: /clan desc <desc>");
                    return true;
                }
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a clan!");
                    return true;
                }
                List promotedlist = getConfig().getStringList("clans" + "." + clan + ".promoted");
                if (!promotedlist.contains(player.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "You are not promoted!");
                    return true;
                }
                String message = "";
                for (int words = 1; words < args.length; words++) {
                    message = message + args[words] + " ";
                }
                getConfig().set("clans" + "." + clan + ".desc", message);
                saveConfig();
                player.sendMessage(ChatColor.GRAY + "You successfully changed the clan description");
                List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                for (Object membersuuid : memberlist) {
                    OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                    if (members.isOnline()) {
                        Player onlinemembers = (Player) members;
                        onlinemembers.sendMessage(ChatColor.DARK_AQUA + player.getName()
                                + " has changed the clan description");
                    }
                }
                return true;
            }
            player.sendMessage(ChatColor.RED + "Unknown argument!");
            return true;
        }

        // Home

        if (cmd.getName().equalsIgnoreCase("home")) {
            if (getConfig().getString("players" + "." + player.getUniqueId().toString() + ".home") == null) {
                player.sendMessage(ChatColor.RED + "You do not have a home set!");
                return true;
            }
            for (Entity e : player.getNearbyEntities(30.0D, 127.0D, 30.0D)) {
                if (e instanceof Player) {
                    Player nearby = (Player) e;
                    String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                    List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
                    if (!memberlist.contains(nearby.getUniqueId().toString())) {
                        player.sendMessage(ChatColor.RED + "You cannot teleport with players nearby!");
                        return true;
                    }
                }
            }
            World world = getServer().getWorld(
                    getConfig().get("players" + "." + player.getUniqueId().toString() + ".home" + ".world").toString());
            double x = getConfig().getDouble("players" + "." + player.getUniqueId().toString() + ".home" + ".x");
            double y = getConfig().getDouble("players" + "." + player.getUniqueId().toString() + ".home" + ".y");
            double z = getConfig().getDouble("players" + "." + player.getUniqueId().toString() + ".home" + ".z");
            float yaw = (float) getConfig().getDouble(
                    "players" + "." + player.getUniqueId().toString() + ".home" + ".yaw");
            float pitch = (float) getConfig().getDouble(
                    "players" + "." + player.getUniqueId().toString() + ".home" + ".pitch");
            Location home = new Location(world, x, y, z, yaw, pitch);
            player.teleport(home);
            player.sendMessage(ChatColor.GRAY + "Teleported to home");
            return true;
        }

        // Sethome

        if (cmd.getName().equalsIgnoreCase("sethome")) {
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".world",
                    player.getLocation().getWorld().getName());
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".x",
                    player.getLocation().getX());
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".y",
                    player.getLocation().getY());
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".z",
                    player.getLocation().getZ());
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".yaw",
                    player.getLocation().getYaw());
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home" + ".pitch",
                    player.getLocation().getPitch());
            saveConfig();
            player.sendMessage(ChatColor.GRAY + "Home set");
            return true;
        }

        // Delhome

        if (cmd.getName().equalsIgnoreCase("delhome")) {
            if (getConfig().getString("players" + "." + player.getUniqueId().toString() + ".home") == null) {
                player.sendMessage(ChatColor.RED + "You do not have a home set!");
                return true;
            }
            getConfig().set("players" + "." + player.getUniqueId().toString() + ".home", null);
            saveConfig();
            player.sendMessage(ChatColor.GRAY + "Home deleted");
            return true;
        }
        return true;
    }

    // Team Chat

    @SuppressWarnings("rawtypes")
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (inChat.contains(player.getName())) {
            event.setCancelled(true);
            String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
            List memberlist = getConfig().getStringList("clans" + "." + clan + ".members");
            for (Object membersuuid : memberlist) {
                OfflinePlayer members = getServer().getOfflinePlayer(UUID.fromString(membersuuid.toString()));
                if (members.isOnline()) {
                    Player onlinemembers = (Player) members;
                    onlinemembers.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Clan"
                            + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + player.getName() + ChatColor.GRAY
                            + ": " + ChatColor.DARK_AQUA + message);
                }
            }
        }
    }

    // Friendly Fire

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            if ((event.getDamager() instanceof Player)) {
                Player enemy = (Player) event.getDamager();
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null)
                    return;
                String enemyclan = getConfig().getString("players" + "." + enemy.getUniqueId().toString() + ".clan");
                if (enemyclan == null)
                    return;
                if ((clan.equals(enemyclan)) && (getConfig().getString("clans" + "." + clan + ".ff").equals("false"))) {
                    event.setCancelled(true);
                }
            }
            if ((event.getDamager() instanceof Arrow)) {
                Projectile shot = (Projectile) event.getDamager();
                if (!(shot.getShooter() instanceof Player))
                    return;
                String clan = getConfig().getString("players" + "." + player.getUniqueId().toString() + ".clan");
                if (clan == null)
                    return;
                Player enemyshooter = (Player) shot.getShooter();
                String enemyclan = getConfig().getString("players." + enemyshooter.getUniqueId() + ".team");
                if (enemyclan == null)
                    return;
                if ((clan.equals(enemyclan)) && (getConfig().getString("clans" + "." + clan + ".ff").equals("false")))
                    event.setCancelled(true);
            }
        }
    }
}