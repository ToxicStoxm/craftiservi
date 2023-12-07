package com.x_tornado10.craftiservi.events.listeners.join_listener;

import com.x_tornado10.craftiservi.craftiservi;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class JoinListener implements Listener {

    private final craftiservi plugin;
    private final HashMap<UUID, String> playerlist;
    public static boolean enabled;

    public JoinListener() {
        plugin = craftiservi.getInstance();
        playerlist = plugin.getPlayerlist();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        if (!playerlist.containsKey(pid)) {

            playerlist.put(pid, formatter.format(date));
            p.sendTitle("§l§6§k2" + ChatColor.DARK_RED + (ChatColor.BOLD + "Welcome to the crafti-servi network!") + "§l§6§k2", ChatColor.GRAY + (ChatColor.BOLD + p.getName() + " joined on >>>" + formatter.format(date)) + "<<<", 0, 200, 10);

        } else {

           show(p);

        }

    }

    private void show(Player player) {

        if (player.isDead()) {return;}

        final int[] timesrun = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                if (timesrun[0] >= 5) {
                    cancel();
                    timesrun[0] = 0;
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§l§6§k2" + ChatColor.DARK_RED + (ChatColor.BOLD + "Welcome back ") + player.getName() + "§l§6§k2"));
                timesrun[0]++;

            }
        }.runTaskTimer(craftiservi.getInstance(), 0, 20);

    }
}
