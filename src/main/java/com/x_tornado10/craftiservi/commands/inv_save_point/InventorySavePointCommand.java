package com.x_tornado10.craftiservi.commands.inv_save_point;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.features.inv_saves.InvSaveMgr;
import com.x_tornado10.craftiservi.logger.Logger;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.inv_request.RestoreRequest;
import com.x_tornado10.craftiservi.utils.statics.PERMISSION;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventorySavePointCommand implements CommandExecutor {

    public static boolean enabled;
    private final craftiservi plugin;
    private final PlayerMessages plmsg;
    private final Logger logger;
    private final InvSaveMgr invSaveMgr;
    private final List<RestoreRequest> restoreRequests;
    private final List<String> illegal_chars;
    private final List<UUID> confirm_list;
    public InventorySavePointCommand() {
        plugin = craftiservi.getInstance();
        plmsg = plugin.getPlayerMessages();
        logger = plugin.getCustomLogger();
        invSaveMgr = plugin.getInvSaveMgr();
        illegal_chars = new ArrayList<>();
        confirm_list = new ArrayList<>();

        illegal_chars.add("\\");
        illegal_chars.add("\"");
        illegal_chars.add(".");
        illegal_chars.add("*");
        illegal_chars.add("$");

        restoreRequests = plugin.getApprovedRequests();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!enabled) {
            if (sender instanceof Player) {
                plmsg.msg((Player) sender, "This command is disabled.");
            } else {
                logger.info("This command is disabled.");
            }
            return true;
        }
        Player p;
        if (!(sender instanceof Player)) {
            logger.info("Coming Soon: This command is not yet supported on console!");
            return true;
        } else {
            p = (Player) sender;
            if (!plugin.hasPermission(p, PERMISSION.COMMAND_INVSAVE)) {
                noPerms(p);
                return true;
            }

            UUID pid = p.getUniqueId();

            switch (args.length) {
                case 0 -> playerSendUsage(p);
                case 1 -> {
                    switch (args[0].toLowerCase()) {
                        case "new" -> plmsg.msg(p, "Please provide a name for this InventorySavePoint!");
                        case "remove" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to remove!");
                        case "rename" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to rename!");
                        case "view" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to review!");
                        case "restore" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to restore!");
                        case "list" -> {
                            ArrayList<String> inv_names = invSaveMgr.getPlayerInvSaves(pid);
                            if (!inv_names.isEmpty()) {
                                plmsg.msg(p, ArrayToStringList(inv_names, ","));
                            } else {
                                plmsg.msg(p,"No InventorySavePoint's were found!");
                            }
                        }
                        default -> playerSendUsage(p);
                    }
                }
                case 2 -> {
                    switch (args[0].toLowerCase()) {
                        case "new" -> {
                            if (isInvalid(args[1])) {
                                plmsg.msg(p,"Name contains Illegal or Invalid Characters! " + ArrayToStringList((ArrayList<String>) illegal_chars, "|"));
                                break;
                            }
                            if (invSaveMgr.exists(pid, args[1])) {
                                plmsg.msg(p, "'" + args[1] + "' does already exist! If you want to rename it do /invsave rename " + args[1] + " <NewInvName>");
                                break;
                            }
                            if (invSaveMgr.add(pid, args[1])) {
                                plmsg.msg(p, "Successfully created new InventorySavePoint '" + args[1] + "'!");
                                logger.info(p.getName() + " created new InventorySavePoint '" + args[1] + "'!");
                            } else {
                                plmsg.msg(p,"Wasn't able to create new InventorySavePoint! Try again");
                            }
                        }
                        case "view" -> {
                            if (notFound(p,args[1])) break;
                            if (invSaveMgr.view(pid, args[1])) {
                                plmsg.msg(p,"Viewing " + args[1] + "...");
                            } else {
                                plmsg.msg(p,"There was an error opening " + args[1] + "! Please try again!");
                            }
                        }
                        case "remove" -> {
                            if (!invSaveMgr.exists(p.getUniqueId(),args[1]) && !args[1].equals("*")) {
                                plmsg.msg(p,"No InventorySavePoint found for the specified name!");
                                break;
                            }
                            if (invSaveMgr.remove(pid, args[1])) {
                                if (args[1].equals("*")) {
                                    plmsg.msg(p, "Successfully deleted all InventorySavePoints!");
                                } else {
                                    plmsg.msg(p, "Successfully deleted '" + args[1] + "'!");
                                    logger.info(p.getName() + " deleted InventorySavePoint '" + args[1] + "'!");
                                }
                            } else {
                                plmsg.msg(p,"There was an error deleting " + args[1] + "! Please try again!");
                            }
                        }
                        case "rename" -> {
                            if (notFound(p,args[1])) break;
                            plmsg.msg(p,"Renaming Failed! Please provide a new name!");
                        }
                        case "restore" -> {
                           if (notFound(p,args[1])) break;
                           if (restoreRequests.contains(new RestoreRequest(pid,args[1]))) {
                               plmsg.msg(p,"Request failed! Another request is already pending!");
                               return true;
                           }
                            if (invSaveMgr.requestRestore(pid, args[1])) {
                                restoreRequests.add(new RestoreRequest(pid,args[1]));
                            }
                        }
                        default -> playerSendUsage(p);
                    }
                }
                case 3 -> {
                    if (args[0].equals("rename")) {
                        if (notFound(p,args[1])) break;
                        if (isInvalid(args[2])) {
                            plmsg.msg(p, ChatColor.RED + "Name contains illegal or invalid characters! " + ArrayToStringList((ArrayList<String>) illegal_chars, "|"));
                            break;
                        }
                        if (invSaveMgr.exists(pid,args[2])) {
                            plmsg.msg(p, ChatColor.RED + "'" + args[2] + "' already exists! Please choose another name!");
                            break;
                        }
                        RestoreRequest rR = new RestoreRequest(pid,args[1]);
                        if (restoreRequests.contains(rR)) {
                            if (!confirm_list.contains(pid)) {
                                plmsg.msg(p, ChatColor.RED + "Renaming '" + args[1] + "' to '" + args[2] + "' will cancel your pending restore request!");
                                plmsg.msg(p, ChatColor.RED + "If you want to proceed anyways, execute the same command again");
                                confirm_list.add(pid);
                                return true;
                            }
                            confirm_list.remove(pid);
                        }
                        if (invSaveMgr.rename(pid,args[1],args[2])) {
                            plmsg.msg(p, "Successfully renamed '" + args[1] + "' to '" + args[2] + "'!");
                            logger.info(p.getName() + " renamed InventorySavePoint '" + args[1] + "' to '" + args[2] + "'!");
                        } else plmsg.msg(p, ChatColor.RED + "Renaming failed! Please try again!");
                    }
                    else playerSendUsage(p);
                }
                default -> plmsg.msg(p, ChatColor.RED + "You provided too many arguments!");
            }

        }

        return true;
    }

    private String ArrayToStringList(ArrayList<String> list, String separator) {
        separator = separator+ " ";
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (String s : list) {
            builder.append(s).append(separator);
        }
        builder.deleteCharAt(builder.lastIndexOf(separator.trim()));
        builder.append("]");
        builder.deleteCharAt(builder.lastIndexOf(" "));
        return builder.toString();
    }

    private void playerSendUsage(Player p) {
        plmsg.msg(p, ChatColor.RED + "Usage: /invsave <new-remove-rename-restore> <InvName> <NewInvName>");
    }

    private void noPerms(Player p) {
        plmsg.msg(p, ChatColor.RED + "You don't have permission to execute this command!");
    }
    private boolean isInvalid(String s) {
        for (String st : illegal_chars) {
            if (s.contains(st)) {return true;}
        }
        return false;
    }
    private boolean notFound(Player p, String name) {
        if (!invSaveMgr.exists(p.getUniqueId(),name)) {
            plmsg.msg(p, ChatColor.RED + "No InventorySavePoint found for the specified name!");
            return true;
        }
        return false;
    }
}
