package com.x_tornado10.commands.open_gui_command;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenGUICommand implements CommandExecutor {

    private final craftiservi plugin = craftiservi.getInstance();
    private final Logger logger = plugin.getCustomLogger();
    private final PlayerMessages plmsg = plugin.getPlayerMessages();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {

            logger.info("Only Players can run this command!");

            return true;
        }

        Player p = (Player) sender;

        if (args.length == 1) {

            String args0 = args[0];
            String[] parts = args0.split("_");

            if (parts.length != 2) {

                sendUsage(p);
                return true;

            }

            String key = parts[0].trim();
            String sid = parts[1].trim();

            switch (key) {
                case "GUI" -> {
                    if (isStringInt(sid)) {

                        int id = Integer.parseInt(sid);

                        HashMap<Integer, Inventory> invs_review = plugin.getInvs_review();

                        if (!invs_review.containsKey(id)) {

                            plmsg.msg(p, "Invalid GUI_ID! (GUI_" + id + ")");
                            break;

                        }

                        Inventory inv = invs_review.get(id);

                        ItemStack approve = new ItemStack(Material.GREEN_CONCRETE);
                        ItemStack deny = new ItemStack(Material.RED_CONCRETE);
                        ItemStack gui_id = new ItemStack(Material.BOOK);

                        ItemMeta approve_meta = approve.getItemMeta();
                        ItemMeta deny_meta = deny.getItemMeta();
                        ItemMeta gui_id_meta = gui_id.getItemMeta();

                        String approve_displayname = "§aApprove request";
                        String deny_displayname = "§cDeny request";
                        String gui_id_dispalyname = "§bGUI_ID";

                        List<String> approve_lore = new ArrayList<>();
                        List<String> deny_lore = new ArrayList<>();
                        List<String> gui_id_lore = new ArrayList<>();

                        approve_lore.add("§7Approve the request and close this inventory");
                        deny_lore.add("§7Deny the request and close this inventory");
                        gui_id_lore.add("§7ID: " + id);

                        approve_meta.setDisplayName(approve_displayname);
                        approve_meta.setLore(approve_lore);

                        deny_meta.setDisplayName(deny_displayname);
                        deny_meta.setLore(deny_lore);

                        gui_id_meta.setDisplayName(gui_id_dispalyname);
                        gui_id_meta.setLore(gui_id_lore);

                        approve.setItemMeta(approve_meta);
                        deny.setItemMeta(deny_meta);
                        gui_id.setItemMeta(gui_id_meta);


                        inv.setItem(53, deny);
                        inv.setItem(52, approve);
                        inv.setItem(51, gui_id);

                        p.openInventory(inv);

                        invs_review.remove(id);

                    } else {

                        sendUsage(p);

                    }
                }
                case "Placeholder" -> {}
                default -> sendUsage(p);
            }

        } else {

            sendUsage(p);

        }

        return true;
    }

    private void sendUsage(Player p) {

        plmsg.msg(p,"Usage: /opengui GUI_ID");

    }


    public boolean isStringInt(String s)
    {
        try {

            Integer.parseInt(s);
            return true;

        } catch (NumberFormatException ex) {

            return false;

        }
    }

}
