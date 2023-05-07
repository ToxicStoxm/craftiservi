package com.x_tornado10.logger;

import com.x_tornado10.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginLogger;

public class Logger {

    private String prefix;

    private craftiservi plugin;
    private ConsoleCommandSender commandSender;
    private PluginLogger logger;
    private static boolean debug;
    private static boolean enabled;

    public Logger(String prefix, boolean debug, boolean enabled) {

        this.prefix = prefix;
        setDebug(debug);
        setEnabled(enabled);
        plugin = craftiservi.getInstance();
        logger = new PluginLogger(plugin);
        commandSender = Bukkit.getConsoleSender();

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
