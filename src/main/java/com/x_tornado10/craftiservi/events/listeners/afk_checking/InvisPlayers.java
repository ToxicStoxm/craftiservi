package com.x_tornado10.craftiservi.events.listeners.afk_checking;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.features.afk_protection.AFKChecker;
import de.tr7zw.changeme.nbtapi.NBT;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

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
    private final HashMap<UUID, UUID> holograms;
    private AFKChecker afkChecker;
    public InvisPlayers() {
        invis_players = new ArrayList<>();
        holograms = new HashMap<>();
        LpAPI = plugin.getLpAPI();
        afkChecker = plugin.getAfkChecker();
    }

    public boolean addInvis(UUID pid, boolean headHolo, boolean headHolo_fullTag, boolean usePe) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        if (!usePe) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Player x = Bukkit.getPlayer(pid);
                if (x == null) {
                    return false;
                }
                pl.hidePlayer(plugin, x);
            }
        } else {
            PotionEffect potionEffect = new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    PotionEffect.INFINITE_DURATION,
                    255,
                    false,
                    false,
                    false
            );
            p.addPotionEffect(potionEffect);
        }
        add(pid);
        if (headHolo) {
            UUID uuid = createHolographicArmorStand(p);
            if (uuid != null) {
                holograms.put(pid, uuid);
                if (headHolo_fullTag) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (invis_players.contains(pid)) {

                            if (LpAPI == null) {
                                LpAPI = plugin.getLpAPI();
                            }
                            ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(holograms.get(pid));
                            String formattedDisplayName = plugin.getDisplayName(pid, p);
                            if (formattedDisplayName == null || formattedDisplayName.contains("null")) {
                                formattedDisplayName = p.getDisplayName();
                            }
                            if (armorStand == null) {
                                return;
                            }
                            armorStand.setCustomName(formattedDisplayName);
                        }
                    }, 40);
                }
            }
        }
        return true;
    }

    public boolean removeInvis(UUID pid) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        for (Player pl : Bukkit.getOnlinePlayers()) {
            Player x = Bukkit.getPlayer(pid);
            if (x == null) {return false;}
            pl.showPlayer(plugin, x);
        }
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        remove(pid);
        if (holograms.containsKey(pid)) {
            deleteHolographicArmorStand(holograms.get(pid));
        }
        holograms.remove(pid);
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

        World world = hologramLocation.getWorld();
        if (world == null) {return null;}
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(player.getDisplayName());
        armorStand.setInvulnerable(true);
        armorStand.setBasePlate(false);
        armorStand.setCollidable(false);
        armorStand.setSilent(true);
        armorStand.setSmall(true);
        armorStand.setMarker(true);
        player.hideEntity(plugin, armorStand);
        NBT.modifyPersistentData(armorStand, nbt -> {nbt.setBoolean("Hologram", true);});
        ItemStack headItem = getPlayerHead(player);
        EntityEquipment es = armorStand.getEquipment();
        if (es == null) return null;
        es.setHelmet(headItem);
        return armorStand.getUniqueId();
    }

    public void deleteHolographicArmorStand(UUID uuid) {
        if (uuid == null) {return;}
        ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(uuid);
        if (armorStand != null) {
            armorStand.remove();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        UUID uuid = pl.getUniqueId();
        for (UUID pid : invis_players) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null) {
                pl.hidePlayer(plugin, p);
            }
        }
        if (!invis_players.contains(uuid)) {
            if (afkChecker == null) {afkChecker = plugin.getAfkChecker();}
            afkChecker.removeAFK(uuid, false, false);
        }
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        Set<UUID> holograms = new HashSet<>();
        for (Map.Entry<UUID,UUID> entry : this.holograms.entrySet()) {
            holograms.add(entry.getValue());
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType().equals(EntityType.ARMOR_STAND) && NBT.getPersistentData(entity, nbt -> nbt.getBoolean("Hologram")) && !holograms.contains(entity.getUniqueId())) {
                    entity.remove();
                }
            }
        }
    }

}
