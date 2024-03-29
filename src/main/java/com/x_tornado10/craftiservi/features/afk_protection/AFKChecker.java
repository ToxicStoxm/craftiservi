package com.x_tornado10.craftiservi.features.afk_protection;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.events.listeners.afk_checking.InvisPlayers;
import com.x_tornado10.craftiservi.logger.Logger;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import com.x_tornado10.craftiservi.utils.statics.GROUP;
import com.x_tornado10.craftiservi.utils.statics.PLACEHOLDER;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AFKChecker implements Listener {

    private final craftiservi plugin = craftiservi.getInstance();
    public static boolean enabled;
    private static boolean broadcastAFK;
    private static boolean broadcastTime;
    private static boolean displayPersonalTime;
    private static boolean AFKeffects;
    private static boolean effects_invisible;
    private static boolean effects_invisible_hholo;
    private static boolean effects_invisible_hholo_fullTag;
    private static boolean effects_AfkNameTag;
    private static boolean effects_invisible_usePe;
    private static String AFKprefix;
    private static String AFKsuffix;
    private static List<String> exclude;
    private int seconds;
    private final ConcurrentHashMap<UUID, Long> playersToCheck;
    private final ConcurrentHashMap<UUID, Long> AFKPlayers;
    private final PlayerMessages plmsg;
    private final Logger logger;
    private final InvisPlayers invisPlayers = plugin.getInvisPlayers();
    private BukkitTask run;
    private LuckPerms LpAPI;

    public AFKChecker() {

        playersToCheck = plugin.getPlayersToCheck();
        AFKPlayers = plugin.getAfkPlayers();
        AFKprefix = "";
        AFKsuffix = "";
        exclude = new ArrayList<>();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        LpAPI = plugin.getLpAPI();

    }

    public boolean isAFK(UUID pid) {
        return AFKPlayers.containsKey(pid);
    }
    public void removeAFK(UUID pid, boolean exclude, boolean messages) {

        if (!exclude) {playersToCheck.put(pid,System.currentTimeMillis());}
        if (getAFKTime(pid) != null) {
            removeAfkEffects(pid, getAFKTime(pid), messages);
        } else {
            removeAfkEffects(pid, System.currentTimeMillis(), messages);
        }
        AFKPlayers.remove(pid);
    }
    public void clearAFK() {
        for (Map.Entry<UUID, Long> entry : AFKPlayers.entrySet()) {
            removeAFK(entry.getKey(), true,true);
        }
    }

    public void addAFK(UUID pid, Long time) {
        AFKPlayers.put(pid,time);
        playersToCheck.remove(pid);
        applyAfkEffects(pid);
    }
    public void removeCheck(UUID pid) {
        playersToCheck.remove(pid);
    }
    public void addCheck(UUID pid) {
        if (!exclude.contains(String.valueOf(pid))) {
            playersToCheck.put(pid,System.currentTimeMillis());
        }
    }
    public Long getAFKTime(UUID pid) {
        return AFKPlayers.get(pid);
    }

    private void cancelTasks() {
        if (run != null) {run.cancel();}
    }
    private void startTasks() {
        afkChecker();
    }

    private void applyAfkEffects(UUID pid) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {
            logger.severe("Error while trying to get Player from UUID!");
            return;
        }
        String name = p.getDisplayName();
        plmsg.msg(p, "You are now §2§l§oAFK§7");
        if (broadcastAFK) {
            logger.broadcast(name + " is now §2§l§oAFK§7", false, new ArrayList<>(Collections.singleton(pid)));
        }
        if (AFKeffects) {
            if (effects_AfkNameTag) {
                if (!plugin.addPlayerToGroup(pid, GROUP.AFKT)) {
                    logger.severe("Error occurred!");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
                    Date date = new Date(getAFKTime(pid));
                    String temp = AFKsuffix;
                    if (temp.contains(PLACEHOLDER.AFK_TIME)) {
                        temp = temp.replace(PLACEHOLDER.AFK_TIME, dateFormat.format(date));
                    }
                    if (!plugin.addSuffixToPlayer(pid, temp)) {
                        logger.severe("Error occurred!");
                    }
                }
            }
            if (effects_invisible) {
                if(!invisPlayers.addInvis(pid, effects_invisible_hholo, effects_invisible_hholo_fullTag, effects_invisible_usePe)) {
                    logger.severe("Error occurred!");
                }
            }
        }

    }
    private void removeAfkEffects(UUID pid, long afkTime, boolean messages) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {
            if (messages) logger.severe("Error while trying to get Player from UUID!");
            return;
        }
        String name = p.getDisplayName();
        if (displayPersonalTime) {
            if (messages) plmsg.msg(p, "You are no longer §2§l§oAFK§7 (AFK time: " +  formatTime(System.currentTimeMillis() - afkTime) + ")");
        } else {
            if (messages) plmsg.msg(p, "You are no longer §2§l§oAFK§7");
        }
        if (broadcastAFK) {
            if (broadcastTime) {
                if (messages) logger.broadcast(name + " is no longer §2§l§oAFK§7 (AFK time: " + formatTime(System.currentTimeMillis() - afkTime) + ")", false, new ArrayList<>(Collections.singleton(pid)));
            } else {
                if (messages) logger.broadcast(name + " is no longer §2§l§oAFK§7", false, new ArrayList<>(Collections.singleton(pid)));
            }
        }
        if (!plugin.removePlayerFromGroup(pid,GROUP.AFKT)) {
            logger.severe("Error occurred!");
        }
        if (!plugin.removeSuffixFromPlayer(pid)) {
            logger.severe("Error occurred!");
        }
        if(!invisPlayers.removeInvis(pid)) {
            logger.severe("Error occurred!");
        }
    }

    private void afkChecker() {

        run = new BukkitRunnable() {
            @Override
            public void run() {
                if (enabled) {

                    if (!playersToCheck.isEmpty()) {

                        for (Map.Entry<UUID, Long> entry : playersToCheck.entrySet()) {

                            UUID pid = entry.getKey();
                            Long lastActivity = entry.getValue();
                            Long currentTime = System.currentTimeMillis();
                            if (currentTime - lastActivity >= seconds * 1000L) {
                                addAFK(pid, currentTime);
                            }
                        }

                    }
                }

            }
        }.runTaskTimer(plugin, 20,20);


    }

    public static String formatTime(long milliseconds) {
        Duration duration = Duration.ofMillis(milliseconds);
        long days = duration.toDaysPart();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d ");
        }
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        if (seconds > 0 || result.isEmpty()) {
            result.append(seconds).append("s ");
        }

        return result.toString().trim();
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        cancelTasks();
        CustomData afkData = e.getData(CDID.AFKC_DATA);
        List<Boolean> b = afkData.getB();
        List<String> s = afkData.getS();
        List<List<String>> lS = afkData.getlS();
        List<String> temp1 = lS.get(0);
        exclude.clear();
        if (!temp1.isEmpty()) {
            for (String st : temp1) {
                try {
                    UUID tempID = UUID.fromString(st);
                    exclude.add(String.valueOf(tempID));
                } catch (IllegalArgumentException ex) {
                    try {
                        UUID tempID = Objects.requireNonNull(Bukkit.getPlayer(st)).getUniqueId();
                        exclude.add(String.valueOf(tempID));
                    } catch (IllegalArgumentException | NullPointerException exe) {
                        try {
                            UUID tempID = Bukkit.getOfflinePlayer(st).getUniqueId();
                            exclude.add(String.valueOf(tempID));
                        } catch (IllegalArgumentException | NullPointerException exep) {
                            logger.warning("Wasn't able to get player specified in the config (Path: Craftiservi.Commands-Features.Afk-Checker.Exclude). Please use a valid UUID!");
                            logger.warning("If you are using the players name, try it with the UUID instead!");
                        }
                    }
                }
            }

        } else {exclude.addAll(temp1);}
        broadcastAFK = b.get(0);
        broadcastTime = b.get(1);
        displayPersonalTime = b.get(2);
        AFKeffects = b.get(3);
        effects_invisible = b.get(7);
        effects_invisible_hholo = b.get(8);
        effects_invisible_hholo_fullTag = b.get(9);
        effects_AfkNameTag = b.get(11);
        enabled = b.get(12);
        effects_invisible_usePe = b.get(13);
        AFKprefix = s.get(0);
        AFKsuffix = s.get(1);
        seconds = afkData.getI(0);

        HashMap<UUID, Long> temp = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID pid = p.getUniqueId();
            if (!exclude.isEmpty()) {
                if (!exclude.contains(String.valueOf(pid))) {
                    if (!AFKPlayers.containsKey(pid) && !p.isDead()) {
                        temp.put(pid, System.currentTimeMillis());
                    }
                } else {
                    removeAFK(pid, true, false);
                }
            } else {
                if (!AFKPlayers.containsKey(pid) && !p.isDead()) {
                    temp.put(pid, System.currentTimeMillis());

                }
            }
        }

        if (LpAPI != null) {
            GroupManager groupManager = LpAPI.getGroupManager();
            if (!groupManager.isLoaded(GROUP.AFKT)) {
                groupManager.loadGroup(GROUP.AFKT);
            }
            Group group = groupManager.getGroup(GROUP.AFKT);

            if (group != null) {
                PrefixNode prefixNode = PrefixNode.builder(AFKprefix, 0).build();
                for (Node node : group.getNodes()) {
                    if (node instanceof PrefixNode) {
                        group.data().remove(node);
                    }
                }
                group.data().add(prefixNode);
            }
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                LpAPI = plugin.getLpAPI();
                GroupManager groupManager = LpAPI.getGroupManager();
                if (!groupManager.isLoaded(GROUP.AFKT)) {
                    groupManager.loadGroup(GROUP.AFKT);
                }
                Group group = groupManager.getGroup(GROUP.AFKT);
                PrefixNode prefixNode = PrefixNode.builder(AFKprefix, 0).build();
                if (group != null) {
                    for (Node node : group.getNodes()) {
                        if (node instanceof PrefixNode) {
                            group.data().remove(node);
                        }
                    }
                    group.data().add(prefixNode);
                }

            }, 100);

        }

        playersToCheck.clear();
        playersToCheck.putAll(temp);
        startTasks();
    }

}
