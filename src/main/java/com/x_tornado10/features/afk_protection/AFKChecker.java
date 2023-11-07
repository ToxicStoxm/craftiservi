package com.x_tornado10.features.afk_protection;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.features.invis_players.InvisPlayers;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.CustomData;
import com.x_tornado10.utils.ObjectCompare;
import com.x_tornado10.utils.TextFormatting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
    private static boolean effects_grayNameTag;
    private static boolean effects_AfkPrefix;
    private static List<String> damageTypes;
    private static String AFKprefix;
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

    public AFKChecker() {

        playersToCheck = plugin.getPlayersToCheck();
        AFKPlayers = plugin.getAfkPlayers();
        exclude = new ArrayList<>();
        temp = new HashMap<>();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        OC = plugin.getOC();
        textFormatting = plugin.getTxtformatting();

    }

    public boolean isAFK(UUID pid) {
        return AFKPlayers.containsKey(pid);
    }
    public void removeAFK(UUID pid, boolean exclude) {

        if (!exclude) {playersToCheck.put(pid,System.currentTimeMillis());}
        removeAfkEffects(pid,getAFKTime(pid));
        AFKPlayers.remove(pid);
        logger.info("REMOVED " + Bukkit.getPlayer(pid).getName());
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

    }

    private void afkChecker() {

        run = new BukkitRunnable() {
            @Override
            public void run() {

                //logger.info("Tick");
                for (Map.Entry entry : playersToCheck.entrySet()) {
                    //logger.info("check " + entry.getKey().toString());
                }
                for (Map.Entry entry : AFKPlayers.entrySet()) {
                    //logger.info("AFK " + entry.getKey().toString());
                }

                if (!playersToCheck.isEmpty()) {

                    for (Map.Entry<UUID,Long> entry : playersToCheck.entrySet()) {

                        UUID pid = entry.getKey();
                        Long lastActivity = entry.getValue();
                        Long currentTime = System.currentTimeMillis();
                        if (currentTime-lastActivity >= seconds * 1000L) {
                            addAFK(pid,currentTime);
                        }
                    }

                }
                if (!AFKPlayers.isEmpty()) {

                    temp.clear();
                    temp.putAll(AFKPlayers);

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
        List<List<String>> lS = afkData.getlS();
        List<String> temp1 = lS.get(0);
        exclude.clear();
        if (!temp1.isEmpty()) {
            for (String s : temp1) {
                try {
                    UUID tempID = UUID.fromString(s);
                    exclude.add(String.valueOf(tempID));
                } catch (IllegalArgumentException ex) {
                    try {
                        UUID tempID = Bukkit.getPlayer(s).getUniqueId();
                        exclude.add(String.valueOf(tempID));
                    } catch (IllegalArgumentException | NullPointerException exe) {
                        try {
                            UUID tempID = Bukkit.getOfflinePlayer(s).getUniqueId();
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
        effects_noCollision = b.get(8);
        effects_grayNameTag = b.get(9);
        effects_AfkPrefix = b.get(10);
        enabled = b.get(11);
        AFKprefix = afkData.getS(0);
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
        playersToCheck.clear();
        playersToCheck.putAll(temp);
        startTasks();
    }

}
