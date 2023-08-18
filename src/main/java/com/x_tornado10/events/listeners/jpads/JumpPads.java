package com.x_tornado10.events.listeners.jpads;

import org.bukkit.Material;
import org.bukkit.Sound;
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

    private double Y_velocity;
    private double velocity_multiplier;
    private final int flyingTimeout = 3;
    public static boolean enabled;

    public JumpPads(double Y_velocity, double velocity_multiplier) {

        this.Y_velocity = Y_velocity;
        this.velocity_multiplier = velocity_multiplier;

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        if (!enabled) {return;}

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();

        if (p.getLocation().getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {

            if (!cooldown.containsKey(pid)) {

                cooldown.put(pid, System.currentTimeMillis());

                Vector v = p.getLocation().getDirection().multiply(velocity_multiplier).setY(Y_velocity);
                p.setVelocity(v);
                p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2f, 1F);

                FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000L));

            } else {

                long timeElapsed = System.currentTimeMillis() - cooldown.get(pid);

                if (timeElapsed >= 500) {

                    cooldown.put(pid, System.currentTimeMillis());

                    Vector v = p.getLocation().getDirection().multiply(velocity_multiplier).setY(Y_velocity);
                    p.setVelocity(v);
                    p.playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2f, 1F);

                    FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000L));

                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!enabled) {return;}
        if (event.getEntity() instanceof Player player) {
            if (FLYING_TIMEOUT.containsKey(player.getUniqueId())) {
                if (FLYING_TIMEOUT.get(player.getUniqueId()) < System.currentTimeMillis()) return;
                event.setCancelled(true);
                player.sendMessage("FallDamage Cancelled!");
            }
        }
    }

    public void updateValues(double Y_velocity, double velocity_multiplier) {

        this.Y_velocity = Y_velocity;
        this.velocity_multiplier = velocity_multiplier;

    }

}
