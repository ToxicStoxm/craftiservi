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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

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
    private HashMap<UUID, Long> playersToCheck;
    private HashMap<UUID, Long> AFKPlayers;
    private PlayerMessages plmsg = plugin.getPlayerMessages();
    private Logger logger = plugin.getCustomLogger();
    private ObjectCompare OC = plugin.getOC();
    private TextFormatting textFormatting = plugin.getTxtformatting();
    private InvisPlayers invisPlayers = plugin.getInvisPlayers();

    public AFKChecker() {

        playersToCheck = plugin.getPlayersToCheck();
        AFKPlayers = plugin.getAfkPlayers();

    }

    public boolean isAFK(UUID pid) {
        return AFKPlayers.containsKey(pid);
    }
    public void removeAFK(UUID pid) {
        AFKPlayers.remove(pid);
    }

    public void addAFK(UUID pid, Long time) {
        AFKPlayers.put(pid,time);
    }
    public Long getAFKTime(UUID pid) {
        return AFKPlayers.get(pid);
    }

    private void cancelTasks() {

    }
    private void startTasks() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID pid = e.getPlayer().getUniqueId();
        if (!playersToCheck.containsKey(pid)) {
            playersToCheck.put(pid, System.currentTimeMillis());}
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID pid = e.getPlayer().getUniqueId();
        playersToCheck.remove(pid);
    }


    @EventHandler
    public void onReload(ReloadEvent e) {
        cancelTasks();
        CustomData afkData = e.getData(2);
        List<Boolean> b = afkData.getB();
        List<List<String>> lS = afkData.getlS();
        exclude = lS.get(0);
        broadcastAFK = b.get(1);
        broadcastTime = b.get(2);
        displayPersonalTime = b.get(3);
        AFKeffects = b.get(4);
        effects_invincible = b.get(5);
        effects_invincible2 = b.get(6);
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
            if (!AFKPlayers.containsKey(pid)) {
                temp.put(pid, System.currentTimeMillis());
            }
        }
        playersToCheck.clear();
        playersToCheck.putAll(temp);
        startTasks();
    }

}
