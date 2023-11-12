package com.x_tornado10.features.afk_protection;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.events.listeners.afk_checking.InvisPlayers;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.CustomData;
import com.x_tornado10.utils.ObjectCompare;
import com.x_tornado10.utils.TextFormatting;
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
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.awt.print.Paper;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AFKChecker implements Listener {

    private craftiservi plugin = craftiservi.getInstance();
    public static boolean enabled;
    private static boolean broadcastAFK;
    private static boolean broadcastTime;
    private static boolean displayPersonalTime;
    private static boolean AFKeffects;
    private static boolean effects_invincible;
    private static boolean effects_invincible2;
    private static boolean effects_invincibleCustom;
    private static boolean effects_invisible;
    private static boolean effects_noCollision;
    private static boolean effects_invisible_hholo;
    private static boolean effects_invisible_hholo_fullTag;
    private static boolean effects_AfkNameTag;
    private static boolean effects_invisible_usePe;
    private static List<String> damageTypes;
    private static String AFKprefix;
    private static String AFKsuffix;
    private static List<String> exclude;
    private int seconds;
    private ConcurrentHashMap<UUID, Long> playersToCheck;
    private ConcurrentHashMap<UUID, Long> AFKPlayers;
    private HashMap<UUID, Long> temp;
    private PlayerMessages plmsg;
    private Logger logger;
    private ObjectCompare OC;
    private TextFormatting textFormatting;
    private InvisPlayers invisPlayers = plugin.getInvisPlayers();
    private BukkitTask run;
    private LuckPerms LpAPI;

    public AFKChecker() {

        playersToCheck = plugin.getPlayersToCheck();
        AFKPlayers = plugin.getAfkPlayers();
        exclude = new ArrayList<>();
        temp = new HashMap<>();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        OC = plugin.getOC();
        textFormatting = plugin.getTxtformatting();
        LpAPI = plugin.getLpAPI();

    }

    public boolean isAFK(UUID pid) {
        return AFKPlayers.containsKey(pid);
    }
    public void removeAFK(UUID pid, boolean exclude) {

        if (!exclude) {playersToCheck.put(pid,System.currentTimeMillis());}
        if (getAFKTime(pid) != null) {
            removeAfkEffects(pid, getAFKTime(pid));
        } else {
            removeAfkEffects(pid, System.currentTimeMillis());
        }
        AFKPlayers.remove(pid);
        logger.info("REMOVED " + Bukkit.getPlayer(pid).getName());
    }
    public void clearAFK() {
        for (Map.Entry<UUID, Long> entry : AFKPlayers.entrySet()) {
            removeAFK(entry.getKey(), true);
        }
    }

    public void addAFK(UUID pid, Long time) {
        AFKPlayers.put(pid,time);
        playersToCheck.remove(pid);
        applyAfkEffects(pid);
        logger.info("ADDED " + Bukkit.getPlayer(pid).getName());
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
                if (!plugin.addPlayerToGroup(pid,"afkTag")) {
                    logger.severe("Error occurred!");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
                    Date date = new Date(getAFKTime(pid));
                    String temp = AFKsuffix;
                    if (temp.contains("%AFK-TIME%")) {
                        temp = temp.replace("%AFK-TIME%", dateFormat.format(date));
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
            if (effects_noCollision) {
                plugin.addPlayerToGroup(pid, "afkNoCollision");
            }
            if (effects_invincible) {
                plugin.addPlayerToGroup(pid, "afkInvincible");
            } else {
                if (effects_invincible2) {
                    plugin.addPlayerToGroup(pid,"afkInvincible2");
                }
            }
        }

    }
    private void removeAfkEffects(UUID pid, long afkTime) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {
            logger.severe("Error while trying to get Player from UUID!");
            return;
        }
        String name = p.getDisplayName();
        if (displayPersonalTime) {
            plmsg.msg(p, "You are no longer §2§l§oAFK§7 (AFK time: " +  formatTime(System.currentTimeMillis() - afkTime) + ")");
        } else {
            plmsg.msg(p, "You are no longer §2§l§oAFK§7");
        }
        if (broadcastAFK) {
            if (broadcastTime) {
                logger.broadcast(name + " is no longer §2§l§oAFK§7 (AFK time: " + formatTime(System.currentTimeMillis() - afkTime) + ")", false, new ArrayList<>(Collections.singleton(pid)));
            } else {
                logger.broadcast(name + " is no longer §2§l§oAFK§7", false, new ArrayList<>(Collections.singleton(pid)));
            }
        }
        if (!plugin.removePlayerFromGroup(pid,"afkTag")) {
            logger.severe("Error occurred!");
        }
        String temp = "";
        if (!plugin.removeSuffixFromPlayer(pid, temp)) {
            logger.severe("Error occurred!");
        }
        if(!invisPlayers.removeInvis(pid)) {
            logger.severe("Error occurred!");
        }
        plugin.removePlayerFromGroup(pid, "afkNoCollision");
        plugin.removePlayerFromGroup(pid,"afkInvincible");
        plugin.removePlayerFromGroup(pid,"afkInvincible2");
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
                    if (!AFKPlayers.isEmpty()) {

                        temp.clear();
                        temp.putAll(AFKPlayers);

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
        CustomData afkData = e.getData(2);
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
                        UUID tempID = Bukkit.getPlayer(st).getUniqueId();
                        exclude.add(String.valueOf(tempID));
                    } catch (IllegalArgumentException | NullPointerException exe) {
                        try {
                            UUID tempID = Bukkit.getOfflinePlayer(st).getUniqueId();
                            exclude.add(String.valueOf(tempID));
                        } catch (IllegalArgumentException | NullPointerException exep) {
                            logger.warning("Wasn't able to get player specified in the config (Path: Craftiservi.Afk-Checker.Exclude). Please use a valid UUID!");
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
        effects_invincible = b.get(4);
        effects_invincible2 = b.get(5);
        effects_invincibleCustom = b.get(6);
        effects_invisible = b.get(7);
        effects_invisible_hholo = b.get(8);
        effects_invisible_hholo_fullTag = b.get(9);
        effects_noCollision = b.get(10);
        effects_AfkNameTag = b.get(11);
        enabled = b.get(12);
        effects_invisible_usePe = b.get(13);
        AFKprefix = s.get(0);
        AFKsuffix = s.get(1);
        damageTypes = lS.get(1);
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
                    removeAFK(pid, true);
                }
            } else {
                if (!AFKPlayers.containsKey(pid) && !p.isDead()) {
                    temp.put(pid, System.currentTimeMillis());

                }
            }
        }

        if (LpAPI != null) {
            GroupManager groupManager = LpAPI.getGroupManager();
            if (!groupManager.isLoaded("afkTag")) {
                groupManager.loadGroup("afkTag");
            }
            Group group = groupManager.getGroup("afkTag");

            PrefixNode prefixNode = PrefixNode.builder(AFKprefix, 0).build();
            for (Node node : group.getNodes()) {
                if (node instanceof PrefixNode) {
                    group.data().remove(node);
                }
            }
            group.data().add(prefixNode);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                LpAPI = plugin.getLpAPI();
                GroupManager groupManager = LpAPI.getGroupManager();
                if (!groupManager.isLoaded("afkTag")) {
                    groupManager.loadGroup("afkTag");
                }
                Group group = groupManager.getGroup("afkTag");

                PrefixNode prefixNode = PrefixNode.builder(AFKprefix, 0).build();
                for (Node node : group.getNodes()) {
                    if (node instanceof PrefixNode) {
                        group.data().remove(node);
                    }
                }
                group.data().add(prefixNode);

            }, 100);

        }

        /*
        if (scM.getMainScoreboard().getTeam("Collision") == null) {
            Collision = scM.getMainScoreboard().registerNewTeam("Collision");
        }
        Collision = scM.getMainScoreboard().getTeam("Collision");
        Collision.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);

         */

        playersToCheck.clear();
        playersToCheck.putAll(temp);
        startTasks();
    }

}
