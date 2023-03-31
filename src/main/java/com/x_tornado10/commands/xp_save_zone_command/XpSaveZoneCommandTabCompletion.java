package com.x_tornado10.commands.xp_save_zone_command;

import com.x_tornado10.craftiservi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;

public class XpSaveZoneCommandTabCompletion implements TabCompleter {

    private final craftiservi pl = craftiservi.getInstance();
    private final HashMap<String, List<Location>> xpsavearea = pl.getXpsaveareas();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> areaargs = new ArrayList<>();

        switch (args.length) {

            case 1 -> {

                switch (args[0].toLowerCase()) {
                    case "a", "ad","add" -> areaargs.add("add");
                    case "r", "re", "rem", "remo", "remov","remove" -> areaargs.add("remove");
                    case "e", "ed", "edi","edit" -> areaargs.add("edit");
                    case "h", "he", "hel","help" -> areaargs.add("help");
                    default -> {
                        areaargs.add("add");
                        areaargs.add("remove");
                        areaargs.add("edit");
                        areaargs.add("help");
                    }
                }

            }
            case 2 -> {

                if (!args[0].toLowerCase().equals("add")) {
                    for (Map.Entry entry : xpsavearea.entrySet()) {

                        areaargs.add(entry.getKey().toString());

                    }

                } else {

                    areaargs.add("<AreaName>");

                }

            }
            case 3 -> {

                for (World world : Bukkit.getWorlds()) {

                    areaargs.add(world.getName());

                }

            }

            case 4, 5 -> {
                if (sender instanceof Player) {

                    Player p = (Player) sender;

                    Double x = p.getLocation().getX();
                    Double y = p.getLocation().getY();
                    Double z = p.getLocation().getZ();

                    DecimalFormat df = new DecimalFormat("#");

                    areaargs.add(df.format(x)+ "|" + df.format(y) + "|" + df.format(z));

                } else {

                    areaargs.add("x|y|z");

                }
            }
            default -> areaargs.add("Too many Arguments!");
        }


        return areaargs;
    }

}
