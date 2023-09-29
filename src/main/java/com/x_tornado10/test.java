package com.x_tornado10;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class test implements Listener {
    Permission perms = craftiservi.getPerms();

    @EventHandler
    public void onjoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        if (perms.has(p, "craftiservi.test")) {

            p.sendMessage("Passed Test!");

        }

    }

}
