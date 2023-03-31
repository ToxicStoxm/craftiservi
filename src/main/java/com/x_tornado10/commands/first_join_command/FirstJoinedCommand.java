package com.x_tornado10.commands.first_join_command;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class FirstJoinedCommand implements CommandExecutor {

    private Player p;

    private Logger logger;

    private craftiservi plugin;

    private HashMap<UUID, String> playerlist;

    private PlayerMessages plmsg;


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        plugin = craftiservi.getInstance();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        playerlist = plugin.getPlayerlist();

        if (!(sender instanceof Player)) {

            if (args.length == 1) {

                if (getUser(args[0])) {

                    logger.info("'" + args[0] + "' first joined on " + playerlist.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));

                } else {

                    logger.info("'" + args[0] + "' never joined or does not exist!");

                }

            } else {

                sendUsage();

            }
            return true;
        }

        p = (Player) sender;

        switch (args.length) {

            case 0 -> plmsg.msg(p,"You first joined on " + playerlist.get(p.getUniqueId()));

            case 1 -> {

                if (getUser(args[0])) {

                    if (playerlist.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()) == null) {

                        plmsg.msg(p,"§7" + args[0] + "§r never joined or does not exist!");

                    } else {

                        plmsg.msg(p,"§7" + args[0] + "§r first joined on " + playerlist.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()));

                    }

                } else {

                    plmsg.msg(p,"§7" + args[0] + "§r never joined or does not exist!");

                }
            }

            default -> playerSendUsage(p);
        }
        return true;
    }

    private void sendUsage() {

        logger.info("Usage: /firstjoin <Player>");

    }

    private void playerSendUsage(Player p) {

        plmsg.msg(p,"Usage: /firstjoin <Player>");

    }


    private boolean getUser(String player) {

        try {

            return playerlist.containsKey(Bukkit.getPlayer(player).getUniqueId());

        } catch (Exception e) {

            return playerlist.containsKey(Bukkit.getOfflinePlayer(player).getUniqueId());

        }
    }

}
