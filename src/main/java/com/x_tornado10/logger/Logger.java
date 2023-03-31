package com.x_tornado10.logger;

import com.x_tornado10.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginLogger;

import java.awt.*;

public class Logger {

    private String prefix;

    private craftiservi plugin;
    private ConsoleCommandSender commandSender = Bukkit.getConsoleSender();
    private Color darkred;
    private PluginLogger logger;

    public Logger(String prefix) {

        this.prefix = prefix;
        plugin = craftiservi.getInstance();
        logger = new PluginLogger(plugin);

    }

    public void info(String message) {

        //logger.info(prefix + message);
        commandSender.sendMessage(prefix + message);

    }

    public void warning(String message) {

        //logger.warning(prefix + "§6" + message + "§r");
        commandSender.sendMessage(prefix + "§6" + message + "§r");

    }

    public void severe(String message) {

        //logger.severe(prefix + "§4" + message + "§r");
        commandSender.sendMessage(prefix + "§4" + message + "§r");


    }

}
