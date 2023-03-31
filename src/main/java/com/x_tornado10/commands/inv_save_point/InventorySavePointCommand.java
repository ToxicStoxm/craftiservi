package com.x_tornado10.commands.inv_save_point;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventorySavePointCommand implements CommandExecutor {

    private Player p;

    private Logger logger;

    private craftiservi plugin;

    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;

    private PlayerMessages plmsg;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        plugin = craftiservi.getInstance();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        inv_saves = plugin.getInv_saves();

        if (!(sender instanceof Player)) {

            logger.info("This command can only be performed by a player!");

            return true;
        }

        p = (Player) sender;
        UUID pid = p.getUniqueId();

        switch (args.length) {
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "new" -> plmsg.msg(p, "Please provide a name for this InventorySavePoint!");
                    case "remove" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to remove!");
                    case "rename" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to rename!");
                }
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "new" -> {
                        if (!inv_saves.containsKey(pid)) {

                            inv_saves.put(pid, new HashMap<>());

                        }
                        HashMap<String,Inventory> p_invs = inv_saves.get(pid);
                        if (args[1].isEmpty()) {

                            playerSendUsage(p);
                            return true;

                        }
                        if (p_invs.containsKey(args[1])) {

                            plmsg.msg(p, "'" + args[1] + "' does already exist! If you want to rename it do /invsave rename " + args[1] + " <NewInvName>");
                            return true;

                        } else {

                            p_invs.put(args[1], inv_point(p));
                            plmsg.msg(p, "Successfully created new InventorySavePoint '" + args[1] + "'!");
                            logger.info(p.getName() + " created new InventorySavePoint '" + args[1] + "'!");

                        }
                    }
                    case "remove" -> {
                        if (!inv_saves.containsKey(pid)) {

                            plmsg.msg(p, "You have no InventorySavePoints to delete!");
                            return true;

                        }
                        HashMap<String, Inventory> p_invs2 = inv_saves.get(pid);
                        if (p_invs2.containsKey(args[1])) {

                            p_invs2.remove(args[1]);
                            plmsg.msg(p, "Successfully deleted '" + args[1] + "'!");
                            logger.info(p.getName() + " deleted InventorySavePoint '" + args[1] + "'!");

                        } else {

                            if (args[1].equals("*")) {

                                if (p_invs2.isEmpty()) {

                                    plmsg.msg(p, "You have no InventorySavePoints to delete!");

                                } else {

                                    p_invs2.clear();
                                    plmsg.msg(p, "Successfully deleted all InventorySavePoints!");
                                }


                            } else {

                                plmsg.msg(p, "Deletion failed! Name '" + args[1] + "' was not found!");
                            }

                        }

                        inv_saves.replace(pid, p_invs2);
                    }
                    case "rename" -> {
                        if (!inv_saves.containsKey(pid)) {

                            plmsg.msg(p, "You have no saved Inventories to rename!");
                            return true;

                        }
                        HashMap<String, Inventory> p_invs3 = inv_saves.get(pid);
                        if (p_invs3.containsKey(args[1])) {

                            plmsg.msg(p, "Renaming Failed! Please provide a new name!");

                        } else {

                            plmsg.msg(p, "Error! Name '" + args[1] + "' was not found!");

                        }
                    }
                    default -> playerSendUsage(p);
                }
            }
            case 3 -> {
                if (args[0].toLowerCase().equals("rename")) {

                    if (!inv_saves.containsKey(pid)) {

                        plmsg.msg(p, "You have no saved Inventories to rename!");
                        return true;

                    }

                    HashMap<String, Inventory> p_invs2 = inv_saves.get(pid);

                    if (p_invs2.containsKey(args[1])) {


                        Inventory temp = p_invs2.get(args[1]);
                        p_invs2.remove(args[1]);
                        p_invs2.put(args[2], temp);
                        plmsg.msg(p, "Successfully renamed '" + args[1] + "' to '" + args[2] + "'!");
                        logger.info(p.getName() + " renamed InventorySavePoint '" + args[1] + "' to '" + args[2] + "'!");

                    } else {

                        plmsg.msg(p, "Error! Name '" + args[1] + "' was not found!");

                    }

                } else {

                    playerSendUsage(p);

                }
            }
            default -> playerSendUsage(p);
        }

        return true;
    }

    private void playerSendUsage(Player p) {

        plmsg.msg(p,"Usage: /invsave <new-remove-rename> <InvName> <NewInvName>");

    }

    private Inventory inv_point(Player p) {

        return p.getInventory();

    }

}
