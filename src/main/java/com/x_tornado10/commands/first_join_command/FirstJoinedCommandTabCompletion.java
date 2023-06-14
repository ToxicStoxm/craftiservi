package com.x_tornado10.commands.first_join_command;

import com.x_tornado10.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class FirstJoinedCommandTabCompletion implements TabCompleter {

    craftiservi plugin = craftiservi.getInstance();
    HashMap<UUID, String> playerlist = plugin.getPlayerlist();

    public static boolean enabled;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!enabled) {return null;}

        ArrayList<String> fjargs = new ArrayList<>();

        if (args.length == 1) {

            for (Map.Entry entry : playerlist.entrySet()) {

                fjargs.add(Bukkit.getOfflinePlayer((UUID) entry.getKey()).getName());

            }

        }


        return fjargs;
    }
}
