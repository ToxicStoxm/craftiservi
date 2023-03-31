package com.x_tornado10.events.listeners;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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


    private final craftiservi plugin = craftiservi.getInstance();
    private HashMap<UUID, String> playerlist = plugin.getPlayerlist();

    private boolean show = false;

    private Logger logger;
    private Player player;
    private UUID pid2;
    private int timesrun = 0;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();
        player = p;
        pid2 = pid;
        e.setJoinMessage("");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        logger = plugin.getCustomLogger();

        if (!playerlist.containsKey(pid)) {

            playerlist.put(pid, formatter.format(date));
            p.sendTitle("§l§6§k2" + ChatColor.DARK_RED + (ChatColor.BOLD + "Welcome to the crafti-servi network!") + "§l§6§k2", ChatColor.GRAY + (ChatColor.BOLD + p.getName() + " joined on >>>" + formatter.format(date)) + "<<<", 0, 200, 10);

        } else {

           show();

        }

    }

    private void show() {

        new BukkitRunnable() {
            @Override
            public void run() {

                if (timesrun>=5) {

                    cancel();
                    timesrun = 0;

                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§l§6§k2" + ChatColor.DARK_RED + (ChatColor.BOLD + "Welcome back ") + Bukkit.getPlayer(pid2).getName() + "§l§6§k2"));

                timesrun++;

            }
        }.runTaskTimer(craftiservi.getInstance(), 0, 20); //in mc ticks

    }
}
