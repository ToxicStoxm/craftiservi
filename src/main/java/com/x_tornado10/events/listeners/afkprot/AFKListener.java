package com.x_tornado10.events.listeners.afkprot;

import com.x_tornado10.craftiservi;
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
import java.util.UUID;

public class AFKListener implements Listener {

    private craftiservi plugin = craftiservi.getInstance();
    private HashMap<UUID, Long> afkList;
    private HashMap<UUID, Long> afkPlayers;
    public static boolean enabled;
    public static boolean allowChat;

    public AFKListener() {
        afkList = plugin.getAfkList();
        afkPlayers = plugin.getAfkPlayers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!afkPlayers.containsKey(player.getUniqueId())) {
                afkList.put(player.getUniqueId(), System.currentTimeMillis());
            }

        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if (!enabled) {
            return;
        }

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        afkList.put(pid, System.currentTimeMillis());
        afkPlayers.remove(pid);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (!enabled) {
            return;
        }

        if (!(e.getEntity() instanceof Player p)) {

            return;

        }

        UUID pid = p.getUniqueId();

        afkList.put(pid, System.currentTimeMillis());
        afkPlayers.remove(pid);


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        if (!enabled) {
            return;
        }

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        afkList.remove(pid);
        afkPlayers.remove(pid);


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        if (!enabled) {
            return;
        }

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        afkList.put(pid, System.currentTimeMillis());
        afkPlayers.remove(pid);

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        if (!enabled || allowChat) {return;}

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        afkList.put(pid, System.currentTimeMillis());
        afkPlayers.remove(pid);

    }

}
