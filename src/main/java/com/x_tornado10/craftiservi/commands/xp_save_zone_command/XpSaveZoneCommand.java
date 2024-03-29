package com.x_tornado10.craftiservi.commands.xp_save_zone_command;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.logger.Logger;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import com.x_tornado10.craftiservi.utils.statics.PERMISSION;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class XpSaveZoneCommand implements CommandExecutor, Listener {

    private final HashMap<String, List<Location>> xpsaveareas;
    private final Logger logger;
    private final PlayerMessages plmsg;
    private HashMap<String, Integer> limits;
    private final craftiservi plugin;
    private HashMap<UUID, Integer> xpAreas;

    private final HashMap<UUID, List<Float>> playersinsavearea;
    public static boolean enabled;
    public XpSaveZoneCommand() {
        plugin = craftiservi.getInstance();
        xpsaveareas = plugin.getXpsaveareas();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        playersinsavearea = plugin.getPlayersinsavearea();
        limits = new HashMap<>();
        xpAreas = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!enabled) {
            if (sender instanceof Player) {
                plmsg.msg((Player) sender, "This command is disabled.");
            } else {
                logger.info("This command is disabled.");
            }
            return true;
        }

        if (!(sender instanceof Player p)) {

            if (args.length == 5) {
                switch (args[0].toLowerCase()) {
                    case "add" -> {
                        if (args[1].contains("|")) {
                            logger.info("Invalid AreaName! Your AreaName can't contain '|'!");
                            return true;
                        }
                        if (xpsaveareas.containsKey(args[1])) {
                            logger.info("Area '" + args[1] + "' does already exist. If you want to edit this Area, try /xparea edit " + args[1] + " <WorldName> <NewLocation> <NewLocation>");
                            return true;
                        }
                        if (!(validLocation(args[3], args[2]) && validLocation(args[4], args[2]))) {
                            logger.info("Please enter a valid location!");
                            return true;
                        }
                        Location loc1 = getLocFromString(args[3], args[2]);
                        Location loc2 = getLocFromString(args[4], args[2]);
                        ArrayList<Location> locs = new ArrayList<>();
                        locs.add(loc1);
                        locs.add(loc2);
                        xpsaveareas.put(args[1], locs);
                        logger.info("Successfully created new XpSaveArea '" + args[1] + "' at " + loc1.getX() + " " + loc1.getY() + " " + loc1.getZ() + " | " + loc2.getX() + " " + loc2.getY() + " " + loc2.getZ() + "!");
                    }
                    case "edit" -> {
                        if (!xpsaveareas.containsKey(args[1])) {

                            logger.info("'" + args[1] + "' does not exist! Please enter a valid AreaName!");
                            return true;

                        }
                        if (!(validLocation(args[3], args[2]) && validLocation(args[4], args[2]))) {

                            logger.info("Please enter a valid location!");
                            return true;

                        }
                        Location loca1 = getLocFromString(args[3], args[2]);
                        Location loca2 = getLocFromString(args[4], args[2]);
                        ArrayList<Location> locas = new ArrayList<>();
                        locas.add(loca1);
                        locas.add(loca2);
                        Location loca1old = xpsaveareas.get(args[1]).get(0);
                        Location loca2old = xpsaveareas.get(args[1]).get(1);
                        xpsaveareas.replace(args[1], locas);
                        logger.info("Successfully modified XpSaveArea '" + args[1] + "'!" + " Old Values: " + loca1old.getX() + " " + loca1old.getY() + " " + loca1old.getZ() + " | " + loca2old.getX() + " " + loca2old.getY() + " " + loca2old.getY() + "  ||  " + "New Values: " + loca1.getX() + " " + loca1.getY() + " " + loca1.getZ() + " | " + loca2.getX() + " " + loca2.getY() + " " + loca2.getZ() + "!");
                    }
                    default -> sendUsage();
                }

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("remove")) {

                    if (xpsaveareas.containsKey(args[1])) {

                        xpsaveareas.remove(args[1]);
                        logger.info("Successfully removed Area '" + args[1] + "'");

                    } else {

                        logger.info("Area '" + args[1] + "' does not exist! Please enter a valid AreaName!");

                    }

                } else {

                    sendUsage();

                }

            } else if (args.length == 1) {

                if (args[0].equalsIgnoreCase("help")) {
                    logger.sendHelp();
                } else {
                    sendUsage();
                }
            } else {
                sendUsage();
            }
        } else {

            if (args.length == 5) {

                switch (args[0].toLowerCase()) {
                    case "add" -> {
                        if (args[1].contains("|")) {
                            plmsg.msg(p, "Invalid AreaName! Your AreaName can't contain '|'!");
                            return true;
                        }
                        if (xpsaveareas.containsKey(args[1])) {
                            plmsg.msg(p, "Area '" + args[1] + "' does already exist. If you want to edit this Area, try /xparea edit " + args[1] + " <WorldName> <NewLocation> <NewLocation>");
                            return true;
                        }
                        if (!(validLocation(args[3], args[2]) && validLocation(args[4], args[2]))) {
                            plmsg.msg(p, "Please enter a valid location!");
                            return true;
                        }
                        Location loc1 = getLocFromString(args[3], args[2]);
                        Location loc2 = getLocFromString(args[4], args[2]);
                        ArrayList<Location> locs = new ArrayList<>();
                        locs.add(loc1);
                        locs.add(loc2);
                        xpsaveareas.put(args[1], locs);
                        xpAreas.put(p.getUniqueId(), xpAreas.containsKey(p.getUniqueId()) ? xpAreas.get(p.getUniqueId()) + 1 : 1);
                        plmsg.msg(p, "Successfully created new XpSaveArea '" + args[1] + "' at " + loc1.getX() + " " + loc1.getY() + " " + loc1.getZ() + " | " + loc2.getX() + " " + loc2.getY() + " " + loc2.getZ() + "!");
                    }
                    case "edit" -> {
                        if (!xpsaveareas.containsKey(args[1])) {
                            plmsg.msg(p, "'" + args[1] + "' does not exist! Please enter a valid AreaName!");
                            return true;
                        }
                        if (!(validLocation(args[3], args[2]) && validLocation(args[4], args[2]))) {
                            plmsg.msg(p, "Please enter a valid location!");
                            return true;
                        }
                        Location loca1 = getLocFromString(args[3], args[2]);
                        Location loca2 = getLocFromString(args[4], args[2]);
                        ArrayList<Location> locas = new ArrayList<>();
                        locas.add(loca1);
                        locas.add(loca2);
                        Location loca1old = xpsaveareas.get(args[1]).get(0);
                        Location loca2old = xpsaveareas.get(args[1]).get(1);
                        xpsaveareas.replace(args[1], locas);
                        plmsg.msg(p, "Successfully modified XpSaveArea '" + args[1] + "'!" + " Old Values: " + loca1old.getX() + " " + loca1old.getY() + " " + loca1old.getZ() + " | " + loca2old.getX() + " " + loca2old.getY() + " " + loca2old.getY() + "  ||  " + "New Values: " + loca1.getX() + " " + loca1.getY() + " " + loca1.getZ() + " | " + loca2.getX() + " " + loca2.getY() + " " + loca2.getZ() + "!");
                    }
                    default -> sendUsage();
                }

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("remove")) {


                    if (xpsaveareas.containsKey(args[1])) {

                        for (Player player : Bukkit.getOnlinePlayers()) {

                            UUID pid = player.getUniqueId();

                            if (locChecker(pid, xpsaveareas.get(args[1]))) {

                                p.setExp(playersinsavearea.get(pid).get(0));
                                p.setLevel(Math.round(playersinsavearea.get(pid).get(1)));

                                plmsg.msg(p, "The XpSaveZone you were within was deleted!");
                                plmsg.msg(p, "Granted levels: " + playersinsavearea.get(pid).get(1));
                                logger.info(p.getName() + " was granted saved levels, because the XpSaveZone the player was in, was deleted!");
                                logger.info("Granted levels: " + playersinsavearea.get(pid).get(1) + " for player " + p.getName());

                                playersinsavearea.remove(pid);
                            }
                        }
                        xpsaveareas.remove(args[1]);
                        xpAreas.put(p.getUniqueId(), xpAreas.containsKey(p.getUniqueId()) ? xpAreas.get(p.getUniqueId()) - 1 : 0);
                        plmsg.msg(p,"Successfully removed Area '" + args[1] + "'");
                    } else {
                        plmsg.msg(p,"Area '" + args[1] + "' does not exist! Please enter a valid AreaName!");
                    }
                } else {
                    playerSendUsage(p);
                }
            } else  if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    plmsg.sendHelp(p);
                } else {
                    playerSendUsage(p);
                }
            } else {
                playerSendUsage(p);
            }
        }
        return true;
    }

    private void sendUsage() {
        logger.info("Usage: /xparea <add-remove-edit-help> <AreaName> <WorldName> <Location> <Location>");
    }

    private void playerSendUsage(Player p) {
        plmsg.msg(p,"Usage: /xparea <add-remove-edit-help> <AreaName> <WorldName> <Location> <Location>");
    }

    private boolean validLocation(String loc, String currentworld) {

        double x;
        double y;
        double z;

        Location location;
        World world;

        try {

            String[] parts = loc.split("\\|");

            x = Double.parseDouble(parts[0]);
            y = Double.parseDouble(parts[1]);
            z = Double.parseDouble(parts[2]);

            world = Bukkit.getWorld(currentworld);

            location = new Location(world, x, y, z);


            Objects.requireNonNull(world).getBlockAt(location);

        } catch (NullPointerException e) {

            return false;

        }

        return true;

    }

    private Location getLocFromString(String loc, String currentworld){

        double x;
        double y;
        double z;

        Location location;
        World world;

            String[] parts = loc.split("\\|");

            x = Double.parseDouble(parts[0]);
            y = Double.parseDouble(parts[1]);
            z = Double.parseDouble(parts[2]);

            world = Bukkit.getWorld(currentworld);

            location = new Location(world, x, y, z);


        return location;

    }

    private boolean locChecker(UUID pid, List<Location> locs) {

        Player p = Bukkit.getPlayer(pid);

        if (p == null) return false;

        Location loc1 = locs.get(0);
        Location loc2 = locs.get(1);
        Location ploc = p.getLocation();

        double x1 = loc1.getX();
        double y1 = loc1.getY();
        double z1 = loc1.getZ();

        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();

        double px = ploc.getX();
        double py = ploc.getY();
        double pz = ploc.getZ();

        boolean x = false;
        boolean y = false;
        boolean z = false;

        if (x1>=x2) if (px>=x2 && px<=x1) x = true;

        if (x1<x2) if (px<=x2 && px>=x1) x = true;

        if (!x) return false;

        if (y1>=y2) if (py>=y2 && py<=y1) y = true;

        if (y1<y2) if (py<=y2 && py>=y1) y = true;

        if (!y) return false;

        if (z1>=z2) if (pz>=z2 && pz<=z1) z = true;

        if (z1<z2) if (pz<=z2 && pz>=z1) z = true;

        return z;

    }
    private boolean checkLimit(Player p) {
        int limit = getLimit(p);
        if (limit == 0) return false;
        UUID pid = p.getUniqueId();
        return !xpAreas.containsKey(pid) || xpAreas.get(pid) < limit;
    }
    @NotNull
    private Integer getLimit(@NotNull Player p) {
        LuckPerms lpAPI = plugin.getLpAPI();
        if (lpAPI == null) return 0;
        UserManager userMgr = lpAPI.getUserManager();
        User user = userMgr.getUser(p.getUniqueId());
        if (user == null) return 0;
        Collection<PermissionNode> nodes = user.getNodes(NodeType.PERMISSION);
        for (PermissionNode node : nodes) {
            String perm = node.getPermission();
            if (perm.contains(PERMISSION.COMMAND_XPAREA_LIMIT)) {
                String[] parts = node.getPermission().split("\\.");
                String key = parts[parts.length - 1];
                if (limits.containsKey(key)) {
                    return limits.get(key);
                } else {
                    return limits.get("default");
                }
            }
        }
        return limits.get("default");
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData xpArea_Data = e.getData(CDID.XPAREA_DATA);
        limits = xpArea_Data.getHashMap();
    }

}
