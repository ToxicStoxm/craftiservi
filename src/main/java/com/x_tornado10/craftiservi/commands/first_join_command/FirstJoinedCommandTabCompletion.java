package com.x_tornado10.craftiservi.commands.first_join_command;

import com.x_tornado10.craftiservi.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FirstJoinedCommandTabCompletion implements TabCompleter {

    private final craftiservi plugin;
    private final HashMap<UUID, String> playerlist;

    public static boolean enabled;
    public FirstJoinedCommandTabCompletion() {
        plugin = craftiservi.getInstance();
        playerlist = plugin.getPlayerlist();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!enabled) {return null;}

        ArrayList<String> fjargs = new ArrayList<>();

        if (args.length == 1) {

            for (Map.Entry<UUID,String> entry : playerlist.entrySet()) {

                fjargs.add(Bukkit.getOfflinePlayer(entry.getKey()).getName());

            }

        }
        return fjargs;
    }
}
