package com.x_tornado10.features.invis_players;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.utils.ObjectCompare;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InvisPlayers implements Listener {

    private final craftiservi plugin = craftiservi.getInstance();
    private static List<UUID> invis_players;
    public void add(UUID player) {
        invis_players.add(player);
    }
    public void remove(UUID player) {
        invis_players.remove(player);
    }
    public List<UUID> getInvis_players() {
        return invis_players;
    }
    private LuckPerms LpAPI;
    private HashMap<UUID, UUID> holograms;
    public InvisPlayers() {
        invis_players = new ArrayList<>();
        holograms = new HashMap<>();
        LpAPI = plugin.getLpAPI();
    }

    public boolean addInvis(UUID pid) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Player x = Bukkit.getPlayer(pid);
            if (x == null) {return false;}
            p.hidePlayer(plugin, x);
        }
        Player p = Bukkit.getPlayer(pid);
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 255, false, false, false);
        p.addPotionEffect(potionEffect);
        add(pid);
        holograms.put(pid, createHolographicArmorStand(p));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (invis_players.contains(pid)) {

                if (LpAPI == null) {
                    LpAPI = plugin.getLpAPI();
                }
                ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(holograms.get(pid));
                UserManager userManager = LpAPI.getUserManager();
                if (!userManager.isLoaded(pid)) {
                    userManager.loadUser(pid);
                }
                User user = userManager.getUser(pid);
                String formattedDisplayName = user.getCachedData().getMetaData().getPrefix() +
                        p.getName() +
                        user.getCachedData().getMetaData().getSuffix();
                armorStand.setCustomName(formattedDisplayName);
                plugin.getCustomLogger().severe("Executed CustomName: '" + formattedDisplayName + "'");
            }
        }, 40);
        plugin.getCustomLogger().severe(invis_players.toString() + " ||||| " + holograms.toString());
        return true;
    }
    public boolean removeInvis(UUID pid) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Player x = Bukkit.getPlayer(pid);
            if (x == null) {return false;}
            p.showPlayer(plugin, x);
        }
        Player p = Bukkit.getPlayer(pid);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        remove(pid);
        if(!deleteHolographicArmorStand(holograms.get(pid))) {
            if (holograms.containsKey(pid)) {plugin.getCustomLogger().severe("ERROR");} else {plugin.getCustomLogger().severe("ERROR2");}
        }
        holograms.remove(pid);
        plugin.getCustomLogger().severe(invis_players.toString() + " ||||| " + holograms.toString());
        return true;
    }

    private ItemStack getPlayerHead(Player player) {
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            headItem.setItemMeta(skullMeta);
        }
        return headItem;
    }

    public UUID createHolographicArmorStand(Player player) {
        Location hologramLocation = player.getLocation();

        ArmorStand armorStand = (ArmorStand) hologramLocation.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(player.getDisplayName());
        armorStand.setInvulnerable(true);
        armorStand.setBasePlate(false);
        armorStand.setCollidable(false);

        ItemStack headItem = getPlayerHead(player);
        armorStand.getEquipment().setHelmet(headItem);
        return armorStand.getUniqueId();
    }

    public boolean deleteHolographicArmorStand(UUID uuid) {
        if (uuid == null) {return false;}
        ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(uuid);
        if (armorStand != null) {
            armorStand.remove();
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        for (UUID pid : invis_players) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null) {
                pl.hidePlayer(plugin, p);
            }
        }
    }

}
