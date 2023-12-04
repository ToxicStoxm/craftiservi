package com.x_tornado10.logger;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.utils.statics.CDID;
import com.x_tornado10.utils.custom_data.reload.CustomData;
import com.x_tornado10.utils.data.convert.TextFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Logger implements Listener {

    private static String prefix;
    private craftiservi plugin;
    private ConsoleCommandSender commandSender;
    private TextFormatting textFormatting;
    private static boolean debug = false;
    private static boolean enabled = true;

    public Logger() {

        plugin = craftiservi.getInstance();
        commandSender = Bukkit.getConsoleSender();
        textFormatting = plugin.getTxtformatting();
        prefix = plugin.getPrefix();

    }

    public void info(String message) {

        if (enabled) {
            //logger.info(prefix + message);
            commandSender.sendMessage(prefix + message);
        }

    }

    public void warning(String message) {

        if (enabled) {

            //logger.warning(prefix + "§6" + message + "§r");
            commandSender.sendMessage(prefix + "§6" + message + "§r");

        }

    }

    public void severe(String message) {

        if (enabled) {


            //logger.severe(prefix + "§4" + message + "§r");
            commandSender.sendMessage(prefix + "§4" + message + "§r");

        }

    }

    public void broadcast(String message, boolean color) {

        info(color ? message : textFormatting.stripColorAndFormattingCodes(message));
        plugin.getPlayerMessages().msg(Bukkit.getOnlinePlayers(), message);

    }

    public void broadcast(String message, boolean color, List<UUID> exclude) {

        info(color ? message : textFormatting.stripColorAndFormattingCodes(message));

        Collection<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        players.removeIf(player -> exclude.contains(player.getUniqueId()));

        plugin.getPlayerMessages().msg(players, message);

    }


    public void debug(String message) {

        if (debug && enabled) {

            info(message);

        }

    }

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData loggerData = e.getData(CDID.LOGGER_DATA);
        prefix = loggerData.getS(0);
        setEnabled(loggerData.getB(0));
        setDebug(loggerData.getB(1));
    }

}
