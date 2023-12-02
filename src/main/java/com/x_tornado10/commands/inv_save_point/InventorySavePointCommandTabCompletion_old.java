package com.x_tornado10.commands.inv_save_point;

import com.x_tornado10.craftiservi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class InventorySavePointCommandTabCompletion_old implements TabCompleter {

    private craftiservi plugin = craftiservi.getInstance();
    private HashMap<UUID, HashMap<String, Inventory>> inv_saves = plugin.getInv_saves();

    private boolean isPlayer = false;

    private Player p;

    public static boolean enabled;
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (!enabled) {return null;}

        List<String> invargs = new ArrayList<>();

        if (sender instanceof Player) {

            p = (Player) sender;
            isPlayer = true;

        }

        switch (args.length) {

            case 1:
                switch (args[0].toLowerCase()) {

                    case "n","ne" -> invargs.add("new");
                    case "rem","remo","remov" -> invargs.add("remove");
                    case "ren","rena","renam" -> invargs.add("rename");
                    case "r","re" -> {

                        invargs.add("remove");
                        invargs.add("rename");

                    }
                    case "v","vi","vie" -> invargs.add("view");
                    case "" -> {
                        invargs.add("new");
                        invargs.add("remove");
                        invargs.add("rename");
                        invargs.add("view");
                    }

                }
                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "new" -> invargs.add("<Name>");
                    case "rename", "view" -> {
                        if (isPlayer && inv_saves.containsKey(p.getUniqueId())) {


                            for (Map.Entry entry : inv_saves.get(p.getUniqueId()).entrySet()) {

                                invargs.add(entry.getKey().toString());

                            }


                        } else {

                            invargs.add("<Name>");

                        }
                    }

                    case "remove" -> {
                        if (isPlayer && inv_saves.containsKey(p.getUniqueId())) {


                            for (Map.Entry entry : inv_saves.get(p.getUniqueId()).entrySet()) {

                                invargs.add(entry.getKey().toString());

                            }

                            invargs.add("*");

                        } else {

                            invargs.add("<Name>");

                        }
                    }

                }
                break;

            case 3:
                if (args[0].toLowerCase().equals("rename")) {
                    invargs.add("<NewName>");
                }
                break;

        }

        return invargs;
    }
}
