package com.x_tornado10.events.listeners.grapling_hook;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.x_tornado10.craftiservi.FLYING_TIMEOUT;

public class GraplingHookListener implements Listener {

    private static final HashMap<UUID, Long> cooldown = new HashMap<>();
    private double cooldown_value = 500;
    private final craftiservi plugin = craftiservi.getInstance();
    private Logger logger = plugin.getCustomLogger();
    private final PlayerMessages plmsg = plugin.getPlayerMessages();
    private final int flyingTimeout = 5;
    private double Y_velocity;
    private boolean prevent_falldmg;
    public static boolean enabled;
    public GraplingHookListener(double Y_velocity) {
        this.Y_velocity = Y_velocity;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent e) {

        if (!enabled) {return;}

        Player p = e.getPlayer();
        ItemStack current = p.getItemInHand();
        ItemMeta current_meta = current.getItemMeta();
        String current_displayname = current_meta.getDisplayName();
        String grappling_hook_dispalyname = "§a§lGrappling Hook";
        UUID pid = p.getUniqueId();

        if (!current_displayname.equals(grappling_hook_dispalyname)) {

            ItemStack grappling_hook = new ItemStack(Material.FISHING_ROD);
            ItemMeta grappling_hook_meta = grappling_hook.getItemMeta();
            grappling_hook_meta.setDisplayName(grappling_hook_dispalyname);
            grappling_hook.setItemMeta(grappling_hook_meta);

            p.getInventory().addItem(grappling_hook);

            return;

        }

        if (e.getState() == PlayerFishEvent.State.IN_GROUND || e.getState() == PlayerFishEvent.State.REEL_IN) {

            if (!cooldown.containsKey(pid)) {

                cooldown.put(pid, System.currentTimeMillis());


                Location loc1 = p.getLocation();
                Location loc2 = e.getHook().getLocation();

                Vector v = new Vector(loc2.getX() - loc1.getX(), Y_velocity, loc2.getZ() - loc1.getZ());
                p.setVelocity(v);

                if (prevent_falldmg) {
                    FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000));
                }

                return;
            }

            long timeElapsed = System.currentTimeMillis() - cooldown.get(pid);

            if (!(timeElapsed >= cooldown_value)) {

                plmsg.msg(p,"§cYou must wait §e0.5s§c between uses! (" + ((cooldown_value - timeElapsed) / 1000) + "s left)");

            } else {

                cooldown.put(pid, System.currentTimeMillis());

                Location loc1 = p.getLocation();
                Location loc2 = e.getHook().getLocation();

                Vector v = new Vector(loc2.getX() - loc1.getX(), Y_velocity, loc2.getZ() - loc1.getZ());
                p.setVelocity(v);

                if (prevent_falldmg) {
                    FLYING_TIMEOUT.put(p.getUniqueId(), System.currentTimeMillis() + (flyingTimeout * 1000L));
                }

            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!enabled || !prevent_falldmg) {return;}
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (FLYING_TIMEOUT.containsKey(player.getUniqueId())) {
                if (FLYING_TIMEOUT.get(player.getUniqueId()) < System.currentTimeMillis()) return;
                event.setCancelled(true);
                FLYING_TIMEOUT.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData GhData = e.getData(4);
        List<Boolean> b = GhData.getB();
        List<Double> d = GhData.getD();

        enabled = b.get(0);
        prevent_falldmg = b.get(1);
        Y_velocity = d.get(0);
        cooldown_value = d.get(1);
    }

}
