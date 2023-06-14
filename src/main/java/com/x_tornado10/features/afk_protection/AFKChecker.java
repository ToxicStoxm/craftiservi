package com.x_tornado10.features.afk_protection;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.HashMapCompare;
import com.x_tornado10.utils.TextFormatting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AFKChecker {

    private craftiservi plugin = craftiservi.getInstance();
    public static boolean enabled;
    private static boolean broadcastAFK;
    private static boolean broadcastTime;
    private static boolean displayPersonalTime;
    private static List<String> exclude;
    private int seconds;
    private HashMap<UUID, Long> afkList;
    private HashMap<UUID, Long> afkPlayers;
    private PlayerMessages plmsg = plugin.getPlayerMessages();
    private Logger logger = plugin.getCustomLogger();
    private BukkitTask run;
    private BukkitTask run2;
    private HashMapCompare HMC = plugin.getHMC();
    private TextFormatting textFormatting = plugin.getTxtformatting();

    public AFKChecker() {

        afkList = plugin.getAfkList();
        afkPlayers = plugin.getAfkPlayers();

    }

    public void checkAFK() {

        run = new BukkitRunnable() {

            @Override
            public void run() {

                if (!enabled) {
                    cancel();
                }

                //logger.info("§bCycle");

                for (Map.Entry<UUID, Long> entry : afkList.entrySet()) {

                    UUID uuid = entry.getKey();
                    long timeElapsed = System.currentTimeMillis() - entry.getValue();

                    if (exclude == null || exclude.isEmpty()) {

                        if (timeElapsed >= seconds * 1000L) {

                            new_afk_player(uuid, System.currentTimeMillis());

                        }

                    } else {

                        List<UUID> exclude_uuids = new ArrayList<>();
                        List<String> exclude_strings = new ArrayList<>();

                        for (String s : exclude) {

                            try {
                                exclude_uuids.add(UUID.fromString(s));
                            } catch (IllegalArgumentException e) {
                                exclude_strings.add(s.toLowerCase());
                            }

                        }

                        if (!exclude_uuids.contains(uuid) && !exclude_strings.contains(Bukkit.getPlayer(uuid).getName().toLowerCase())) {

                            if (timeElapsed >= seconds * 1000L) {

                                new_afk_player(uuid, System.currentTimeMillis());

                            }

                        }
                    }

                }

            }

        }.runTaskTimer(plugin, 20, 20);

    }

    public boolean startCheck() {

        if (!enabled) {
            return true;
        }
        try {
            try {
                run.cancel();
                run2.cancel();
            } catch (Exception e) {
                checkAFK();
                applyAFK();
                return true;
            }
            checkAFK();
            applyAFK();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public void updateValues(int seconds, List<String> exclude, boolean broadcastAFK, boolean broadcastTime, boolean displayPersonalTime) {
        this.seconds = seconds;
        AFKChecker.broadcastAFK = broadcastAFK;
        AFKChecker.broadcastTime = broadcastTime;
        AFKChecker.displayPersonalTime = displayPersonalTime;
        AFKChecker.exclude = exclude;

        for (Map.Entry<UUID, Long> entry : afkPlayers.entrySet()) {

            long timeElapsed = System.currentTimeMillis() - entry.getValue();

            if (!(timeElapsed > seconds * 1000L)) {
                afkPlayers.remove(entry.getKey());
            }

        }
    }


    private void new_afk_player(UUID uuid, long start_time) {
        if (!afkPlayers.containsKey(uuid)) {afkPlayers.put(uuid, start_time);}
    }

    private void applyAFK() {

        HashMap<UUID, Long> temp = new HashMap<>();

        run2 = new BukkitRunnable() {
            @Override
            public void run() {

                if (!enabled) {
                    cancel();
                }

                HashMap<String, HashMap<UUID, Long>> result = HMC.compare(temp, afkPlayers);
                HashMap<UUID, Long> added = result.get("added");
                HashMap<UUID, Long> removed = result.get("removed");

                applyAFK(added, removed);

                temp.clear();
                temp.putAll(afkPlayers);

            }
        }.runTaskTimer(plugin, 20, 20);

    }

    private void applyAFK(HashMap<UUID, Long> players_added, HashMap<UUID, Long> players_removed) {

        List<UUID> uuids_added = new ArrayList<>();
        List<UUID> uuids_removed = new ArrayList<>();
        for (Map.Entry<UUID, Long> entry : players_added.entrySet()) {
            uuids_added.add(entry.getKey());
        }
        for (Map.Entry<UUID, Long> entry : players_removed.entrySet()) {
            uuids_removed.add(entry.getKey());
        }

        for (UUID target : uuids_added) {

            Player p = Bukkit.getPlayer(target);
            String name = p.getName();
            if (broadcastAFK) {
                logger.broadcast(name + " is now AFK!", false, new ArrayList<>(Collections.singleton(target)));
            }
            plmsg.msg(p, "You are now AFK!");

        }

        for (UUID target : uuids_removed) {

            Player p;
            String name;

            try {
                p = Bukkit.getPlayer(target);
                name = p.getName();
            } catch (NullPointerException e) {
                return;
            }

            if (broadcastAFK) {
                if (broadcastTime) {
                    logger.broadcast(name + " is no longer AFK! (AFK time: " + textFormatting.getDurationBreakdown(System.currentTimeMillis() - players_removed.get(target))  + ")", false, new ArrayList<>(Collections.singleton(target)));
                } else {
                    logger.broadcast(name + " is no longer AFK!", false, new ArrayList<>(Collections.singleton(target)));
                }
            }
            if (displayPersonalTime) {
                plmsg.msg(p, "You are no longer AFK! (AFK time: " + textFormatting.getDurationBreakdown(System.currentTimeMillis() - players_removed.get(target)) + ")");
            } else {
                plmsg.msg(p, "You are no longer AFK!");
            }

        }


    }

}
