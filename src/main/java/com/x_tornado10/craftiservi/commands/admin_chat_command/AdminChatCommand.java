package com.x_tornado10.craftiservi.commands.admin_chat_command;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.message_sys.OpMessages;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.statics.PERMISSION;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminChatCommand implements CommandExecutor {
    private final OpMessages opmsg;
    private final PlayerMessages plmsg;
    private final craftiservi plugin;
    public AdminChatCommand() {
        plugin = craftiservi.getInstance();
        opmsg = plugin.getOpmsg();
        plmsg = plugin.getPlayerMessages();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player p) {
            if (!plugin.hasPermission(p, PERMISSION.COMMAND_ADMINCHAT)) {
                noPerms(p);
                return true;
            }
            if (args.length == 0) {
                plmsg.msg(p,"Please provide a message!");
            }
            if (args.length == 1) {
                opmsg.send(p, args[0]);
            }
            if (args.length >= 2) {
                plmsg.msg(p,"Too many arguments!");
            }
        } else {
            if (args.length == 0) {
                sender.sendMessage("Please provide a message!");
            }
            if (args.length == 1) {
                opmsg.send(sender.getName(), args[0]);
            }
            if (args.length >= 2) {
                sender.sendMessage("Too many arguments!");
            }
        }
        return true;
    }
    private void noPerms(Player p) {
        plmsg.msg(p, "You don't have the permission to execute this command!");
    }
}
