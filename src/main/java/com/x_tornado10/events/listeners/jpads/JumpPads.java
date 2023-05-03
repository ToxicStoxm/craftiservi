package com.x_tornado10.events.listeners.jpads;

import com.x_tornado10.events.listeners.PlayerMoveListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

import static com.x_tornado10.craftiservi.FLYING_TIMEOUT;

public class JumpPads implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    private final int flyingTimeout = 3;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        if (p.getLocation().getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {

            if (!cooldown.containsKey(pid)) {

                cooldown.put(pid, System.currentTimeMillis());

                Vector v = p.getLocation().getDirection().multiply(2.00).setY(1.2);
                p.setVelocity(v);
                p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2f, 1F);

                FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000L));

            } else {

                long timeElapsed = System.currentTimeMillis() - cooldown.get(pid);

                if (timeElapsed >= 500) {

                    cooldown.put(pid, System.currentTimeMillis());

                    Vector v = p.getLocation().getDirection().multiply(2.00).setY(1.2);
                    p.setVelocity(v);
                    p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2f, 1F);

                    FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000L));

                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (FLYING_TIMEOUT.containsKey(player.getUniqueId())) {
                if (FLYING_TIMEOUT.get(player.getUniqueId()) < System.currentTimeMillis()) return;
                event.setCancelled(true);
            }
        }
    }
}
