package com.x_tornado10.messages;

import com.x_tornado10.events.custom.ReloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerMessages implements Listener {

    private String prefix;

    private String line = "§7---------------------------------------§r";

    public PlayerMessages (String prefix) {

        this.prefix = prefix;

    }

    public void msg(Player player, String message) {

        player.sendMessage(prefix + "§7" + message + "§r");

    }

    public void msg(ArrayList<Player> players, String message) {

        for (Player player : players) {

            msg(player ,"§7" + message + "§r");

        }

    }

    public void msg(Collection<? extends Player> players, String message) {

        for (Player player : players) {

            msg(player , "§7" + message + "§r");

        }

    }

    public void line(Player p) {

        msg(p,line);

    }

    public String getLine() {
        return line;
    }

    public void msg_inlines(Player p, String message) {

        line(p);
        msg(p,message);
        line(p);

    }

    @EventHandler
    public void onReload(ReloadEvent e) {

        prefix = e.getData(1).getS(0);

    }

}
