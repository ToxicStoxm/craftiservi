package com.x_tornado10.events.listeners.afkprot;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.features.afk_protection.AFKChecker;
import com.x_tornado10.utils.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AFKListener implements Listener {

    private craftiservi plugin = craftiservi.getInstance();
    private HashMap<UUID, Long> afkList;
    private HashMap<UUID, Long> afkPlayers;
    private HashMap<UUID, Long> playersToCheck;
    private AFKChecker checker;
    public static boolean enabled;
    public static boolean allowChat;

    public AFKListener() {
        afkPlayers = plugin.getAfkPlayers();
        playersToCheck = plugin.getPlayersToCheck();
        checker = plugin.getAfkChecker();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!enabled) {return;}
        UUID pid = e.getPlayer().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid);
        }
        if (playersToCheck.containsKey(pid)) {
            playersToCheck.put(pid,System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (!enabled) {return;}

        if (!(e.getEntity() instanceof Player p)) {return;}

        UUID pid = e.getEntity().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid);
        }
        if (playersToCheck.containsKey(pid)) {
            playersToCheck.put(pid,System.currentTimeMillis());
        }

    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData afkData = e.getData(6);
        List<Boolean> b = afkData.getB();

        enabled = b.get(0);
        allowChat = b.get(1);

    }

}
