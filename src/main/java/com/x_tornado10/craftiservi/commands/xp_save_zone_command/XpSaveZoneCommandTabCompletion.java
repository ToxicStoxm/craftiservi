package com.x_tornado10.craftiservi.commands.xp_save_zone_command;

import com.x_tornado10.craftiservi.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public class XpSaveZoneCommandTabCompletion implements TabCompleter {

    private final craftiservi plugin;
    private final HashMap<String, List<Location>> xpsavearea;
    public static boolean enabled;
    public XpSaveZoneCommandTabCompletion() {
        plugin = craftiservi.getInstance();
        xpsavearea = plugin.getXpsaveareas();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!enabled) {return null;}

        List<String> xparea_args = new ArrayList<>();

        switch (args.length) {

            case 1 -> {

                switch (args[0].toLowerCase()) {
                    case "a", "ad","add" -> xparea_args.add("add");
                    case "r", "re", "rem", "remo", "remov","remove" -> xparea_args.add("remove");
                    case "e", "ed", "edi","edit" -> xparea_args.add("edit");
                    case "h", "he", "hel","help" -> xparea_args.add("help");
                    default -> {
                        xparea_args.add("add");
                        xparea_args.add("remove");
                        xparea_args.add("edit");
                        xparea_args.add("help");
                    }
                }

            }
            case 2 -> {

                if (!args[0].equalsIgnoreCase("add")) {
                    for (Map.Entry<String, List<Location>> entry : xpsavearea.entrySet()) {

                        xparea_args.add(entry.getKey());

                    }

                } else {

                    xparea_args.add("<AreaName>");

                }

            }
            case 3 -> {

                for (World world : Bukkit.getWorlds()) {

                    xparea_args.add(world.getName());

                }

            }

            case 4, 5 -> {
                if (sender instanceof Player p) {

                    Double x = p.getLocation().getX();
                    Double y = p.getLocation().getY();
                    Double z = p.getLocation().getZ();

                    DecimalFormat df = new DecimalFormat("#");

                    xparea_args.add(df.format(x)+ "|" + df.format(y) + "|" + df.format(z));

                } else {

                    xparea_args.add("x|y|z");

                }
            }
            default -> xparea_args.add("Too many Arguments!");
        }


        return xparea_args;
    }

}
