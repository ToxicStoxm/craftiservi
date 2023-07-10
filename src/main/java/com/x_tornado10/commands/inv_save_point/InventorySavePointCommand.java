package com.x_tornado10.commands.inv_save_point;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class InventorySavePointCommand implements CommandExecutor {

    private Player p;

    private Logger logger;

    private craftiservi plugin;

    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;

    private PlayerMessages plmsg;
    public static boolean enabled;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        plugin = craftiservi.getInstance();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        inv_saves = plugin.getInv_saves();

        if (!enabled) {

            if (sender instanceof Player) {

                plmsg.msg((Player) sender, "This command is disabled.");

            } else {

                logger.info("This command is disabled.");

            }
            return true;
        }

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
                    case "review" -> plmsg.msg(p, "Please specify which InventorySavePoint you want to review!");
                    default -> playerSendUsage(p);
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

                            if(checkName(args[1])) {

                                p_invs.put(args[1], inv_point(p));
                                plmsg.msg(p, "Successfully created new InventorySavePoint '" + args[1] + "'!");
                                logger.info(p.getName() + " created new InventorySavePoint '" + args[1] + "'!");

                            } else {

                                plmsg.msg(p, "Illegal symbol/s detected! Name can't contain: . | \" | \\");
                                return true;

                            }

                        }
                    }
                    case "view" -> {
                        if (!inv_saves.containsKey(pid)) {

                            plmsg.msg(p,"You have no InventorySavePoints to review!");
                            return true;

                        }
                        HashMap<String, Inventory> p_invs = inv_saves.get(pid);
                        if (p_invs.containsKey(args[1])) {

                            Inventory inv = p_invs.get(args[1]);
                            ItemStack[] slots = inv.getContents();

                            Inventory saved_inv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), Bukkit.getPlayer(pid).getName());
                            saved_inv.setContents(slots);

                            ItemStack restore = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                            ItemMeta restore_meta = restore.getItemMeta();
                            restore_meta.setDisplayName("§aRestore Inventory");

                            List<String> restore_lore = new ArrayList<>();
                            restore_lore.add("§7Restores the saved Inventory and drops the items of your current inventory!");
                            restore_meta.setLore(restore_lore);
                            restore.setItemMeta(restore_meta);

                            saved_inv.setItem(53, restore);

                            p.openInventory(saved_inv);

                        } else {

                            if (p_invs.isEmpty()) {

                                plmsg.msg(p, "You have no InventorySavePoints to review!");

                            } else {

                                plmsg.msg(p, "Error! Name '" + args[1] + "' was not found!");

                            }

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

                        if (checkName(args[2])) {

                            Inventory temp = p_invs2.get(args[1]);
                            p_invs2.remove(args[1]);
                            p_invs2.put(args[2], temp);
                            plmsg.msg(p, "Successfully renamed '" + args[1] + "' to '" + args[2] + "'!");
                            logger.info(p.getName() + " renamed InventorySavePoint '" + args[1] + "' to '" + args[2] + "'!");
                        } else {

                            plmsg.msg(p, "Illegal symbol/s detected! Name can't contain: . | \" | \\");
                            return true;

                        }

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

        Inventory temp = p.getInventory();

        Inventory inv = Bukkit.createInventory(p, 54, p.getName());

        ItemStack[] slots = temp.getContents();

        inv.setContents(slots);

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta info_meta = info.getItemMeta();
        List<String> info_lore = new ArrayList<>();

        Date date1 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        double yaw = p.getLocation().getYaw();
        double pitch = p.getLocation().getPitch();

        DecimalFormat df = new DecimalFormat("#.##");


        info_meta.setDisplayName("§9§lInventorySavePoint Info v1.0");
        info_lore.add("§8§lCreation Date:§r§7 " + sdf.format(date1));
        info_lore.add("§8§lLocation:§r§7 x[" + df.format(x) + "] y[" + df.format(y) + "] z[" + df.format(z) + "] yaw[" + df.format(yaw) + "] pitch[" + df.format(pitch) + "]");
        info_lore.add("§8§lWorld:§r§7 " + p.getWorld().getName());
        info_lore.add("§8§lXp-Level:§r§7 " + p.getLevel());
        info_lore.add("§8§lHealth:§r§7 " + p.getHealth());
        info_lore.add("§8§lGamemode:§r§7 " + p.getGameMode());
        info_meta.setLore(info_lore);
        info.setItemMeta(info_meta);

        inv.setItem(45, info);

        return inv;

    }

    private boolean checkName(String name) {

        List<String> illegalSymbols = new ArrayList<>();
        illegalSymbols.add("\\");
        illegalSymbols.add("\"");
        illegalSymbols.add(".");

        for (String s : illegalSymbols) {

            if (name.contains(s)) {

                return false;

            }

        }

        return true;
    }

}
