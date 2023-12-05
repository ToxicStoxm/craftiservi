package com.x_tornado10.message_sys;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.utils.statics.CDID;
import com.x_tornado10.utils.custom_data.reload.CustomData;
import com.x_tornado10.utils.statics.GROUP;
import com.x_tornado10.utils.statics.PERMISSION;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OpMessages implements Listener {

    private final craftiservi plugin;
    private final String def_prefix;
    private final String def_short_prefix;
    private static boolean enabled;
    private String prefix;
    private boolean auto_assign;
    private boolean useCustomPrefix;
    private boolean shortPrefix;
    private String customPrefix;
    private List<String> admins;
    private List<UUID> online_admins;
    private Logger logger;
    private BukkitTask run;
    private BukkitTask run2;
    private ConcurrentHashMap<Long, Object> queue;
    private double queueTime;
    private double queueLimit;
    private boolean queue_;
    private static boolean queueOutput = false;

    public OpMessages () {
        plugin = craftiservi.getInstance();
        admins = new ArrayList<>();
        online_admins = new ArrayList<>();
        def_prefix = "§c§l[Admin-Chat]§r ";
        def_short_prefix = "§c§l[OC]§r ";
        prefix = def_prefix;
        logger = plugin.getCustomLogger();
        queue = new ConcurrentHashMap<>();
    }

    public void send(String message) {
        if (!enabled) {
            return;
        }
        if (online_admins.isEmpty() && queue_) {
            if (queueLimit != -1) if (queue.size() >= queueLimit) return;
            queue.put(System.currentTimeMillis(), message);
            check_queue();
            return;
        }
        for (UUID pid : online_admins) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null) {
                p.sendMessage(prefix + message);
            }
        }
    }
    public void send(BaseComponent[] baseComponents) {
        if (!enabled) {
            return;
        }
        if (online_admins.isEmpty() && queue_) {
            if (queueLimit != -1) if (queue.size() >= queueLimit) return;
            queue.put(System.currentTimeMillis(), baseComponents);
            check_queue();
            return;
        }
        for (UUID pid : online_admins) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null) {
                p.spigot().sendMessage(baseComponents);
            }
        }
    }

    public void send(Player p, String message) {
        send(Objects.requireNonNull(plugin.getDisplayName(p.getUniqueId(), p)).replace("null", "") + ": " + message);
    }
    public void send(UUID pid, String message) {
        Player p = Bukkit.getPlayer(pid);
        if (p != null) {
            send(Objects.requireNonNull(plugin.getDisplayName(pid, p)).replace("null","") + ": " + message);
        }
    }
    public void send(String sender_name, String message) {
        send(sender_name + ": " + message);
    }
    public void send(String line, UUID pid, String name) {
        BaseComponent[] baseComponents =
                new ComponentBuilder()
                        .append(prefix + line + "\n" + prefix + ChatColor.GRAY + "Restore request -> " + Bukkit.getOfflinePlayer(pid).getName() + " - ")
                        .append(String.valueOf(ChatColor.BOLD) + ChatColor.AQUA + ChatColor.UNDERLINE + name + ChatColor.RESET)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/opengui GUI$" + pid + ":" + name))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Click to open Inventory. GUI_" + name)))
                        .append("\n" + prefix + line)
                        .create();
        send(baseComponents);
    }

    private void update_admins() {
        run = new BukkitRunnable() {
            List<UUID> temp = new ArrayList<>();
            @Override
            public void run() {
                if (!enabled) {return;}
                temp.clear();
                Set<OfflinePlayer> ops = Bukkit.getOperators();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (ops.contains(p) || plugin.isPlayerInGroup(p.getUniqueId(), GROUP.ADMIN)) {
                        temp.add(p.getUniqueId());
                    }
                }
                online_admins.clear();
                online_admins.addAll(temp);
            }
        }.runTaskTimerAsynchronously(plugin,20,40);
    }
    private void queueOutput(List<Object> queue) {
        logger.severe("queueOutput");
        logger.severe(String.valueOf(getPeriod(queue.size())));
        new BukkitRunnable() {
            @Override
            public void run() {
                logger.severe(String.valueOf(queue.size()));
                if (!enabled) {queueOutput = false; return; }
                if (queue.isEmpty()) {cancel();queueOutput = false;return;}
                if (online_admins.isEmpty()) {
                    queueAdd(queue);
                    cancel();
                    queueOutput = false;
                } else {
                    Object object = queue.get(queue.size() - 1);
                    if (object instanceof String) {
                        send((String) queue.get(queue.size() - 1));
                    } else if (object instanceof BaseComponent[]) {
                        send((BaseComponent[]) queue.get(queue.size() - 1));
                    }
                    queue.remove(queue.size() - 1);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20, getPeriod(queue.size()));
    }
    private void check_queue() {
        run2 = new BukkitRunnable() {
            @Override
            public void run() {
                if (!enabled) {return;}
               if (!queue.isEmpty()) {
                   if (online_admins.isEmpty()) {
                       for (Map.Entry<Long, Object> entry : queue.entrySet()) {
                           if (entry.getKey() - System.currentTimeMillis() > queueTime) {
                               queue.remove(entry.getKey());
                           }
                       }
                   } else {
                       if (!queueOutput) {
                           List<Object> queueEntries = new ArrayList<>();
                           for (Map.Entry<Long, Object> entry : queue.entrySet()) {
                               queueEntries.add(entry.getValue());
                               queue.remove(entry.getKey());
                           }
                           queueOutput(queueEntries);
                           queueOutput = true;
                       }
                   }
               }
            }
        }.runTaskTimerAsynchronously(plugin,20,20);
    }
    public boolean isAdmin(UUID pid) {
        return online_admins.contains(pid);
    }
    private void queueAdd(List<Object> temp) {
        for (Object s : temp) {
            queue.put(System.currentTimeMillis(), s);
        }
    }

    private int getPeriod(int queueSize) {
        int result = 10;
        int entries = queueSize / 10;

        for (int i = 0; i < entries; i++) {
            if (i == 3 || i == 2) {
                result -= 2;
            } else {
                result -= 1;
            }
        }

        return Math.max(0, result);
    }

    @EventHandler
    public void onReload(ReloadEvent e) {

        if (run != null) run.cancel();
        if (run2 != null) run2.cancel();
        CustomData opmsg_data = e.getData(CDID.OPMSG_DATA);
        List<Boolean> b = opmsg_data.getB();
        List<Double> d = opmsg_data.getD();
        enabled = b.get(0);
        auto_assign = b.get(1);
        useCustomPrefix = b.get(2);
        shortPrefix = b.get(3);
        queue_ = b.get(4);
        admins = opmsg_data.getLS(0);
        customPrefix = opmsg_data.getS(0);
        queueTime = d.get(0) * 60 * 1000;
        queueLimit = d.get(1);

        if (useCustomPrefix && shortPrefix || !useCustomPrefix && shortPrefix) {
            prefix = def_short_prefix;
        } else if (useCustomPrefix) {
            prefix = customPrefix;
        } else {
            prefix = def_prefix;
        }
        online_admins.clear();
        if (!auto_assign && !admins.isEmpty()) {
            for (String s : admins) {
                try {
                    UUID tempID = UUID.fromString(s);
                    online_admins.add(tempID);
                } catch (IllegalArgumentException ex) {
                    try {
                        UUID tempID = Bukkit.getPlayer(s).getUniqueId();
                        online_admins.add(tempID);
                    } catch (IllegalArgumentException | NullPointerException exe) {
                        try {
                            UUID tempID = Bukkit.getOfflinePlayer(s).getUniqueId();
                            online_admins.add(tempID);
                        } catch (IllegalArgumentException | NullPointerException exep) {
                            logger.warning("Wasn't able to get player specified in the config (Path: Craftiservi.Chat.Admins). Please use a valid UUID!");
                            logger.warning("If you are using the players name, try it with the UUID instead!");
                        }
                    }
                }
            }
        }
        if (auto_assign) {
            Set<OfflinePlayer> ops = Bukkit.getOperators();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (ops.contains(p) || plugin.isPlayerInGroup(p.getUniqueId(), GROUP.ADMIN) || plugin.hasPermission(p, PERMISSION.CRAFTISERVI_ADMIN)) {
                    online_admins.add(p.getUniqueId());
                }
            }
            update_admins();
        }
        if (!queue.isEmpty()) {
            check_queue();
        }

    }
}
