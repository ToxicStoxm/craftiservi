package com.x_tornado10.commands.main_command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> tabComplete = new ArrayList<>();

        if (args.length == 1) {

            switch (args[0].toLowerCase()) {
                case "", "r", "re" -> {
                    tabComplete.add("reloadconfig");
                    tabComplete.add("resetconfig");
                    tabComplete.add("restoreconfig");
                    tabComplete.add("help");
                }
                case "res" -> {
                    tabComplete.add("resetconfig");
                    tabComplete.add("restoreconfig");
                }
                case "rel", "relo", "reloa", "reload", "reloadc", "reloadco", "reloadcon", "reloadconf", "reloadconfi" -> tabComplete.add("reloadconfig");
                case "rese", "reset", "resetc", "resetco", "resetconf", "resetconfi" -> tabComplete.add("resetconfig");
                case "rest", "resto", "restor", "restore", "restorec", "restoreco", "restorecon", "restoreconf", "restoreconfi" -> tabComplete.add("restoreconfig");
                case "h", "he", "hel" -> tabComplete.add("help");
            }

        }

        return tabComplete;
    }
}
