package com.x_tornado10.craftiservi.commands.inv_save_point;

import com.x_tornado10.craftiservi.craftiservi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventorySavePointCommandTabCompletion implements TabCompleter {

    private final craftiservi plugin;
    private final HashMap<UUID, HashMap<String, Inventory>> inv_saves;

    public InventorySavePointCommandTabCompletion() {
        plugin = craftiservi.getInstance();
        inv_saves = plugin.getInv_saves();
    }

    public static boolean enabled;
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!enabled) {return null;}

        List<String> invargs = new ArrayList<>();

        Player p = null;
        if (sender instanceof Player ) {
            p = (Player) sender;
        }

        switch (args.length) {
            case 1 -> {
                switch (args[0].toLowerCase()) {

                    case "n", "ne" -> invargs.add("new");
                    case "rem", "remo", "remov" -> invargs.add("remove");
                    case "ren", "rena", "renam" -> invargs.add("rename");
                    case "r", "re" -> {
                        invargs.add("remove");
                        invargs.add("rename");
                        invargs.add("restore");
                    }
                    case "res", "rest", "resto", "restor" -> invargs.add("restore");
                    case "l", "li", "lis" -> invargs.add("list");
                    case "v", "vi", "vie" -> invargs.add("view");
                    case "" -> {
                        invargs.add("new");
                        invargs.add("remove");
                        invargs.add("rename");
                        invargs.add("view");
                        invargs.add("list");
                        invargs.add("restore");
                    }

                }
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "new" -> invargs.add("<Name>");
                    case "rename", "view", "restore" -> {
                        if (p != null && inv_saves.containsKey(p.getUniqueId())) {
                            for (Map.Entry<String, Inventory> entry : inv_saves.get(p.getUniqueId()).entrySet()) {
                                invargs.add(entry.getKey());
                            }
                        } else {
                            invargs.add("<Name>");
                        }
                    }

                    case "remove" -> {
                        if (p != null && inv_saves.containsKey(p.getUniqueId())) {
                            for (Map.Entry<String, Inventory> entry : inv_saves.get(p.getUniqueId()).entrySet()) {
                                invargs.add(entry.getKey());
                            }
                            invargs.add("*");
                        } else {
                            invargs.add("<Name>");
                        }
                    }
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("rename")) {
                    invargs.add("<NewName>");
                }
            }
        }

        return invargs;
    }
}
