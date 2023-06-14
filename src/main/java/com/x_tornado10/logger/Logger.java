package com.x_tornado10.logger;

import com.x_tornado10.craftiservi;
import com.x_tornado10.utils.TextFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Logger {

    private String prefix;
    private String dc_prefix;
    private craftiservi plugin;
    private ConsoleCommandSender commandSender;
    private PluginLogger logger;
    private TextFormatting textFormatting;
    private static boolean debug;
    private static boolean enabled;

    public Logger(String prefix, String dc_prefix, boolean debug, boolean enabled) {

        this.prefix = prefix;
        this.dc_prefix = dc_prefix;
        setDebug(debug);
        setEnabled(enabled);
        plugin = craftiservi.getInstance();
        logger = new PluginLogger(plugin);
        commandSender = Bukkit.getConsoleSender();
        textFormatting = plugin.getTxtformatting();

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

    public void dc_info(String message) {

        if (enabled) {
            //logger.info(prefix + message);
            commandSender.sendMessage(dc_prefix + message);
        }

    }

    public void dc_warning(String message) {

        if (enabled) {

            //logger.warning(prefix + "§6" + message + "§r");
            commandSender.sendMessage(dc_prefix + "§6" + message + "§r");

        }

    }

    public void dc_severe(String message) {

        if (enabled) {

            //logger.severe(prefix + "§4" + message + "§r");
            commandSender.sendMessage(dc_prefix + "§4" + message + "§r");

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
    public void nodebug(String message) {

        if (!debug && enabled) {

            info(message);

        }

    }
    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }
    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }
    public void upDateValues(String prefix) {

        this.prefix = prefix;

    }

}
