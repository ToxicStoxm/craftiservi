package com.x_tornado10.craftiservi.message_sys;

import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerMessages implements Listener {

    private String prefix;

    private final String line = "§7---------------------------------------§r";
    private final BaseComponent[] helpmessage;

    public PlayerMessages (String prefix) {

        this.prefix = prefix;
        helpmessage = new ComponentBuilder()
                .append(getLine() + "\n")
                .append(ChatColor.GRAY + "Plugin Documentation: ")
                .append(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "github.com" + "\n")
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/ToxicStoxm/craftiservi/blob/main/README.md"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("https://github.com/ToxicStoxm/craftiservi/blob/main/README.md")))
                .append(getLine())
                .create();

    }

    public void msg(Player player, String message) {

        player.sendMessage(prefix + "§7" + message + "§r");

    }

    public void msg(ArrayList<Player> players, String message) {

        for (Player player : players) {

            msg(player ,"§7" + message + "§r");

        }

    }
    public void msg(Player p, BaseComponent[] baseComponents) {
        p.spigot().sendMessage(baseComponents);
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

    public void sendHelp(Player p) {msg(p,helpmessage);}

    @EventHandler
    public void onReload(ReloadEvent e) {

        prefix = e.getData(CDID.PLMSG_DATA).getS(0);

    }

}
