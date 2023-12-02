package com.x_tornado10.events.listeners.xp_area_listener;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.message_sys.PlayerMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.text.DecimalFormat;
import java.util.*;

public class PlayerMoveListener implements Listener {

    private final craftiservi pl = craftiservi.getInstance();
    private HashMap<String, List<Location>> xpsaveareas = pl.getXpsaveareas();

    private HashMap<UUID, List<Float>> playersinsavearea = pl.getPlayersinsavearea();
    private PlayerMessages plmsg = pl.getPlayerMessages();
    private Logger logger = pl.getCustomLogger();
    public static boolean enabled;

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {

        UUID pid = e.getPlayer().getUniqueId();
        Player p = e.getPlayer();

        for (Map.Entry<String, List<Location>> entry : xpsaveareas.entrySet()) {

            if (locChecker(pid, entry.getValue())) {

                if (!playersinsavearea.containsKey(pid)) {

                    float exp = p.getExp();
                    float xplvl = p.getLevel();
                    List<Float> xps = new ArrayList<>();
                    xps.add(exp);
                    xps.add(xplvl);

                    playersinsavearea.put(pid, xps);

                    plmsg.msg(p, "You entered XpSaveZone: '" + entry.getKey() + "'");
                    plmsg.msg(p, "Saved levels: " + playersinsavearea.get(pid).get(1));
                    logger.info(p.getName() + " entered xpSaveZone: '" + entry.getKey() + "'");
                    logger.info("Saved levels: " + playersinsavearea.get(pid).get(1) + " for player " + p.getName());

                    p.setLevel(0);
                    p.setExp(0);

                }

            } else {

                if (playersinsavearea.containsKey(pid)) {


                    p.setExp(playersinsavearea.get(pid).get(0));
                    p.setLevel(Math.round(playersinsavearea.get(pid).get(1)));

                    plmsg.msg(p, "You left XpSaveZone: '" + entry.getKey() + "'");
                    plmsg.msg(p, "Granted levels: " + playersinsavearea.get(pid).get(1));
                    logger.info(p.getName() + " left xpSaveZone: '" + entry.getKey() + "'");
                    logger.info("Granted levels: " + playersinsavearea.get(pid).get(1) + " for player " + p.getName());

                    playersinsavearea.remove(pid);

                }

            }

        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();
        UUID pid = p.getUniqueId();

        if (playersinsavearea.containsKey(pid)) {

            e.setDroppedExp(0);
            plmsg.msg(p, "You died within an XpSaveZone! Leave the Zone to get Your Levels back!");
            DecimalFormat df = new DecimalFormat("#.#");
            logger.info(p.getName() + " died within an XpSaveZone! At: " + df.format(p.getLocation().getX()) + "|" + df.format(p.getLocation().getY()) + "|" + df.format(p.getLocation().getZ()) + " in world '" + p.getLocation().getWorld().getName() + "'");

        }

    }

    @EventHandler
    public void onXpCollected(PlayerExpChangeEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        if (playersinsavearea.containsKey(pid)) {

            p.setExp(0F);
            p.setLevel(0);

        }

    }

    @EventHandler
    public void onLevelChanged(PlayerLevelChangeEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        if (playersinsavearea.containsKey(pid)) {

            p.setExp(0F);
            p.setLevel(0);

        }

    }

    @EventHandler
    public void onJoin (PlayerJoinEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        if (playersinsavearea.containsKey(pid)) {

            if (p.isDead()) {

                p.spigot().respawn();

            }

            p.setExp(playersinsavearea.get(pid).get(0));
            p.setLevel(Math.round(playersinsavearea.get(pid).get(1)));
            playersinsavearea.remove(pid);

        }

    }


    private boolean locChecker(UUID pid, List<Location> locs) {

        Player p = Bukkit.getPlayer(pid);

        Location loc1 = locs.get(0);
        Location loc2 = locs.get(1);
        Location ploc = p.getLocation();

        double x1 = loc1.getX();
        double y1 = loc1.getY();
        double z1 = loc1.getZ();

        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();

        double px = ploc.getX();
        double py = ploc.getY();
        double pz = ploc.getZ();

        boolean x = false;
        boolean y = false;
        boolean z = false;

        if (x1>=x2) {

            if (px>=x2 && px<=x1) {

                x = true;

            }

        }

        if (x1<x2) {

            if (px<=x2 && px>=x1) {

                x = true;

            }

        }

        if (!x) {

            return false;

        }

        if (y1>=y2) {

            if (py>=y2 && py<=y1) {

                y = true;

            }

        }

        if (y1<y2) {

            if (py<=y2 && py>=y1) {

                y = true;

            }

        }

        if (!y) {

            return false;

        }

        if (z1>=z2) {

            if (pz>=z2 && pz<=z1) {

                z = true;

            }

        }

        if (z1<z2) {

            if (pz<=z2 && pz>=z1) {

                z = true;

            }

        }



        return z;

    }

}
