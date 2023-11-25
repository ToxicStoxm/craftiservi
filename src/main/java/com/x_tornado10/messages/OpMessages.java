package com.x_tornado10.messages;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.utils.CDID;
import com.x_tornado10.utils.CustomData;
import com.x_tornado10.utils.GROUP;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

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
    private HashMap<String, Long> queue;
    private double queueTime;
    private double queueLimit;
    private boolean queue_;

    public OpMessages () {

        plugin = craftiservi.getInstance();
        admins = new ArrayList<>();
        online_admins = new ArrayList<>();
        def_prefix = "&c&l[Admin-Chat]:&r ";
        def_short_prefix = "&c&l[OC]:&r ";
        prefix = def_prefix;
        logger = plugin.getCustomLogger();
        queue = new HashMap<>();
    }

    public void send(String message) {
        if (!enabled) {return;}
        if (online_admins.isEmpty() && queue_) {
            if (queueLimit != -1) if (queue.size() >= queueLimit) return;
            queue.put(message,System.currentTimeMillis());
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
    public void send(Player p, String message) {
        send(plugin.getDisplayName(p.getUniqueId(), p) + message);
    }
    public void send(UUID pid, String message) {
        Player p = Bukkit.getPlayer(pid);
        if (p != null) {
            send(plugin.getDisplayName(pid, p) + message);
        }
    }
    public void send(String sender_name, String message) {
        send(sender_name + message);
    }

    private void update_admins() {
        run = new BukkitRunnable() {
            List<UUID> temp = new ArrayList<>();
            @Override
            public void run() {
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
    private void queueOutput(List<String> queue) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (queue.isEmpty()) {cancel();}
                if (online_admins.isEmpty()) {
                    queueAdd(queue);
                    cancel();
                }
                send(queue.get(queue.size()-1));
                queue.remove(queue.size()-1);
            }
        }.runTaskTimerAsynchronously(plugin, 0,10);
    }
    private void check_queue() {
        run2 = new BukkitRunnable() {
            @Override
            public void run() {
               if (!queue.isEmpty()) {
                   if (online_admins.isEmpty()) {
                       for (Map.Entry<String, Long> entry : queue.entrySet()) {
                           if (entry.getValue() - System.currentTimeMillis() > queueTime) {
                               queue.remove(entry.getKey());
                           }
                       }
                   } else {
                       List<String> queueEntries = new ArrayList<>();
                       for (Map.Entry<String, Long> entry : queue.entrySet()) {
                               queueEntries.add(entry.getKey());
                               queue.remove(entry.getKey());
                       }
                       queueOutput(queueEntries);
                   }
               }
            }
        }.runTaskTimerAsynchronously(plugin,20,20);
    }
    private void queueAdd(List<String> temp) {
        for (String s : temp) {
            queue.put(s, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onReload(ReloadEvent e) {

        run.cancel();
        run2.cancel();
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
                if (ops.contains(p) || plugin.isPlayerInGroup(p.getUniqueId(), GROUP.ADMIN)) {
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
