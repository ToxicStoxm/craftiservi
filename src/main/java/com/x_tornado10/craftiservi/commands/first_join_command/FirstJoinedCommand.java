package com.x_tornado10.craftiservi.commands.first_join_command;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.logger.Logger;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class FirstJoinedCommand implements CommandExecutor {

    private final Logger logger;

    private final craftiservi plugin;

    private final HashMap<UUID, String> playerlist;

    private final PlayerMessages plmsg;
    public static boolean enabled;

    public FirstJoinedCommand() {
        plugin = craftiservi.getInstance();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        playerlist = plugin.getPlayerlist();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!enabled) {
            if (sender instanceof Player) {
                plmsg.msg((Player) sender, "This command is disabled.");
            } else {
                logger.info("This command is disabled.");
            }
            return true;
        }


        if (!(sender instanceof Player p)) {
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

        Player p = Bukkit.getPlayer(player);
        OfflinePlayer oP;
        UUID pid;
        if (p == null) {
            oP = Bukkit.getOfflinePlayer(player);
            pid = oP.getUniqueId();
        } else {
             pid = p.getUniqueId();
        }

        return playerlist.containsKey(pid);
    }

}
