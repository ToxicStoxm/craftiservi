package com.x_tornado10.craftiservi.logger;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.data.convert.TextFormatting;
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
    private final craftiservi plugin;
    private final ConsoleCommandSender commandSender;
    private final TextFormatting textFormatting;
    private static boolean debug = false;
    private static boolean enabled = true;
    private final String logger_helpmessage;

    public Logger() {
        plugin = craftiservi.getInstance();
        commandSender = Bukkit.getConsoleSender();
        textFormatting = plugin.getTxtformatting();
        prefix = plugin.getPrefix();
        logger_helpmessage = "Please visit https://github.com/ToxicStoxm/craftiservi/blob/main/README.md for extended documentation.";
    }

    public void info(String message) {
        if (enabled) {
            commandSender.sendMessage(prefix + message);
        }

    }

    public void warning(String message) {

        if (enabled) {
            commandSender.sendMessage(prefix + "§6" + message + "§r");
        }

    }

    public void severe(String message) {

        if (enabled) {
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
    public void sendHelp() {info(logger_helpmessage);}

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
