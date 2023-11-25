package com.x_tornado10.events.listeners.afk_checking;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.features.afk_protection.AFKChecker;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.utils.CDID;
import com.x_tornado10.utils.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
    private static boolean allowBlockPlace;
    private static boolean allowBlockBreak;
    private static boolean allowHitMob;
    private static boolean allowHitPlayer;
    private static boolean killCreepers;
    private static boolean stopCreeperTarget;
    private List<String> dTypes;

    public AFKListener() {
        afkPlayers = plugin.getAfkPlayers();
        playersToCheck = plugin.getPlayersToCheck();
        checker = plugin.getAfkChecker();
        logger = plugin.getCustomLogger();
        dTypes = new ArrayList<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!enabled) {
            return;
        }

        Player p = e.getPlayer();
        UUID pid = p.getUniqueId();
        if (checker.isAFK(pid)) {

            if (effects_noCollision) {
                Location start = e.getFrom().getBlock().getLocation();
                Location end = e.getTo().getBlock().getLocation();
                double pitch_start = e.getFrom().getPitch();
                double yaw_start = e.getFrom().getYaw();
                double pitch_end = e.getTo().getPitch();
                double yaw_end = e.getTo().getYaw();
                if (start != end && pitch_start == pitch_end && yaw_start == yaw_end) {
                    e.setCancelled(true);
                } else {
                    checker.removeAFK(pid, false, true);
                    if (playersToCheck.containsKey(pid)) {
                        checker.addCheck(pid);
                    }
                }
                return;
            }
            checker.removeAFK(pid, false, true);
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!enabled) {return;}
        if (!(e.getEntity() instanceof Player p)) {
            if (e.getDamager() instanceof Player pl) {
                UUID plid = pl.getUniqueId();
                if (checker.isAFK(plid)) {
                    if (allowHitMob && allowHitPlayer) {return;}
                    if (allowHitMob) {return;}
                    checker.removeAFK(plid, false, true);
                    if (playersToCheck.containsKey(plid)) {
                        checker.addCheck(plid);
                    }
                    e.setCancelled(true);
                }
            }
            return;
        } else {
            if (e.getDamager() instanceof Player pl) {
                UUID plid = pl.getUniqueId();
                if (checker.isAFK(plid)) {
                    if (e.getEntity() instanceof Player && !allowHitPlayer) {
                        checker.removeAFK(plid, false, true);
                        if (playersToCheck.containsKey(plid)) {
                            checker.addCheck(plid);
                        }
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (((Player) e.getEntity()).getHealth() <= e.getFinalDamage()) {return;}

        UUID pid = p.getUniqueId();

        if (checker.isAFK(pid)) {
            if (effects_invincible) {
                e.setCancelled(true);
            } else if (effects_invincible2) {
                if (!(e.getDamager() instanceof Player)) {
                    e.setCancelled(true);
                } else {
                    checker.removeAFK(pid, false, true);
                    if (playersToCheck.containsKey(pid)) {
                        checker.addCheck(pid);
                    }
                }
            } else if (effects_invincibleCustom && dTypes.contains(e.getCause().name())) {
                e.setCancelled(true);
            } else {
                checker.removeAFK(pid, false, true);
                if (playersToCheck.containsKey(pid)) {
                    checker.addCheck(pid);
                }
            }
        }

    }

    @EventHandler
    public void onCreeperTarget(EntityTargetLivingEntityEvent e) {
        if (!enabled) {return;}
        if (!effects_noCollision) {return;}
        if (!e.getEntityType().equals(EntityType.CREEPER)) {return;}
        Creeper c = (Creeper) e.getEntity();
        LivingEntity target = e.getTarget();
        if (!(target instanceof Player p)) {return;}
        if (checker.isAFK(p.getUniqueId())) {
            if (stopCreeperTarget) {
                c.setTarget(null);
                e.setCancelled(true);
                if (killCreepers ) {
                    Location c_loc = c.getLocation();
                    Location p_loc = p.getLocation();
                    if (c_loc.distance(p_loc) < 6) {
                        c.remove();
                    }
                }
            } else {
                if (killCreepers) {
                    c.remove();
                }
            }

        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        if (!enabled) {return;}
        if (!(e.getEntity() instanceof Player p)) {return;}
        if (((Player) e.getEntity()).getHealth() <= e.getFinalDamage()) {return;}

        UUID pid = e.getEntity().getUniqueId();

        if (checker.isAFK(pid)) {
            if (effects_invincible) {
               e.setCancelled(true);
            } else if (effects_invincible2) {
                if (!(e instanceof EntityDamageByEntityEvent)) {
                    e.setCancelled(true);
                }
            } else {
                checker.removeAFK(pid, false, true);
                if (playersToCheck.containsKey(pid)) {
                    checker.addCheck(pid);
                }
            }
        }


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        if (!enabled) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,true, true);
        } else {
            checker.removeCheck(pid);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!enabled) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (!checker.isAFK(pid) && !e.getPlayer().isDead()) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!enabled) {return;}
        if (allowBlockBreak) {return;}
        UUID pid = e.getPlayer().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,false, true);
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!enabled) {return;}
        if (allowBlockPlace) {return;}
        UUID pid = e.getPlayer().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,false, true);
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        if (!enabled || allowChat) {return;}

        UUID pid = e.getPlayer().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,false, true);
        }
        if (playersToCheck.containsKey(pid)) {
            checker.addCheck(pid);
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!enabled) {return;}
        UUID pid = e.getEntity().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,true, true);
        } else {
            checker.removeCheck(pid);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (!enabled) {return;}
        UUID pid = e.getPlayer().getUniqueId();
        if (checker.isAFK(pid)) {
            checker.removeAFK(pid,false, true);
        } else {
            checker.addCheck(pid);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        if (!enabled) {return;}
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (checker.isAFK(p.getUniqueId())) {
                PotionEffect newEffect = e.getNewEffect();
                if (newEffect != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (!enabled) {
            return;
        }
        Player p = e.getPlayer();
        if (!checker.isAFK(p.getUniqueId())) {
            return;
        }
        if (e.getReason().toLowerCase().contains("flying")) {
            e.setCancelled(true);
            e.setLeaveMessage("");
            Location playerLocation = p.getLocation();
            World world = p.getWorld();

            for (int y = playerLocation.getBlockY(); y >= -64; y--) {
                Location loc = new Location(world, playerLocation.getBlockX(), y, playerLocation.getBlockZ());
                Block block = loc.getBlock();

                if (block.getType().isSolid()) {
                    loc.setX(playerLocation.getX());
                    loc.setZ(playerLocation.getZ());
                    loc.setYaw(playerLocation.getYaw());
                    loc.setPitch(playerLocation.getPitch());
                    p.teleport(loc.add(0, 1, 0));
                    break;
                }
                if (y == -64) {
                    p.kickPlayer("You were floating over the void for too long!");
                }
            }
        }
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData afkData = e.getData(CDID.AFKL_DATA);
        List<Boolean> b = afkData.getB();

        enabled = b.get(0);
        allowChat = b.get(1);
        effects_invincible = b.get(2);
        effects_invincible2 = b.get(3);
        effects_invincibleCustom = b.get(4);
        effects_noCollision = b.get(5);
        allowHitMob = b.get(6);
        allowHitPlayer = b.get(7);
        allowBlockPlace = b.get(8);
        allowBlockBreak = b.get(9);
        stopCreeperTarget = b.get(10);
        killCreepers = b.get(11);
        dTypes = afkData.getLS(0);

    }

}
