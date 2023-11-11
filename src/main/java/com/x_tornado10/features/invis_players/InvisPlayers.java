package com.x_tornado10.features.invis_players;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.utils.ObjectCompare;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InvisPlayers {

    private final craftiservi plugin = craftiservi.getInstance();
    private ObjectCompare OC = plugin.getOC();
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
    private BukkitTask run;
    public InvisPlayers() {
        invis_players = new ArrayList<>();
        check();
    }

    private void check() {

        List<UUID> temp = new ArrayList<>();

        run = new BukkitRunnable() {
            @Override
            public void run() {

                HashMap<String, List<UUID>> result = OC.compare(temp, invis_players);
                List<UUID> added = result.get("added");
                List<UUID> removed = result.get("removed");

                applyInvis(added, removed);

                temp.clear();
                temp.addAll(invis_players);
            }
        }.runTaskTimer(plugin, 20, 5);

    }

    private void applyInvis(List<UUID> added, List<UUID> removed) {

        for (Player p : Bukkit.getOnlinePlayers()) {

            for (UUID uuid : added) {
                try {
                    Player x = Bukkit.getPlayer(uuid);
                    p.hidePlayer(plugin, x);
                } catch (NullPointerException e) {
                    remove(uuid);
                }
            }

            for (UUID uuid : removed) {
                try {
                    Player x = Bukkit.getPlayer(uuid);
                    p.showPlayer(plugin, x);
                } catch (NullPointerException e) {
                    remove(uuid);
                }
            }

        }

    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        run.cancel();
        check();
    }

}
