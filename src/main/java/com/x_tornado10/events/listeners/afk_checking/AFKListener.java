package com.x_tornado10.events.listeners.afk_checking;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.features.afk_protection.AFKChecker;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.utils.CustomData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKListener implements Listener {

    private craftiservi plugin = craftiservi.getInstance();
    private ConcurrentHashMap<UUID, Long> afkPlayers;
    private ConcurrentHashMap<UUID, Long> playersToCheck;
    private AFKChecker checker;
    public static boolean enabled;
    private Logger logger;
    public static boolean allowChat;
    private static boolean effects_noCollision;
    private static boolean effects_invincible;
    private static boolean effects_invincible2;
    private static boolean effects_invincibleCustom;

    public AFKListener() {
        afkPlayers = plugin.getAfkPlayers();
        playersToCheck = plugin.getPlayersToCheck();
        checker = plugin.getAfkChecker();
        logger = plugin.getCustomLogger();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!enabled) {
            return;
        }

        UUID pid = e.getPlayer().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid, false);
            logger.info("onMove Removed");
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (!enabled) {return;}

        if (!(e.getEntity() instanceof Player p)) {return;}

        if (((Player) e.getEntity()).getHealth() <= e.getFinalDamage()) {return;}

        UUID pid = e.getEntity().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid,false);
            logger.info("onEntityDamage Removed");
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        if (!enabled) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid,true);
            logger.info("onPlayerQuit Removed");
        } else {
            checker.removeCheck(pid);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!enabled) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (!afkPlayers.containsKey(pid) && !e.getPlayer().isDead()) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        if (!enabled || allowChat) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid,false);
            logger.info("onChat Removed");
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!enabled) {return;}
        UUID pid = e.getEntity().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid,true);
        } else {
            checker.removeCheck(pid);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (!enabled) {return;}
        UUID pid = e.getPlayer().getUniqueId();
        if (afkPlayers.containsKey(pid)) {
            checker.removeAFK(pid,false);
        } else {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData afkData = e.getData(6);
        List<Boolean> b = afkData.getB();

        enabled = b.get(0);
        allowChat = b.get(1);
        effects_invincible = b.get(2);
        effects_invincible2 = b.get(3);
        effects_invincibleCustom = b.get(4);
        effects_noCollision = b.get(5);

    }

}
