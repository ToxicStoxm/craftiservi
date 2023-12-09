package com.x_tornado10.craftiservi.commands.open_gui_command;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.features.inv_saves.InvSaveMgr;
import com.x_tornado10.craftiservi.logger.Logger;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.inv_request.RestoreRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OpenGUICommand implements CommandExecutor {

    private final craftiservi plugin;
    private final Logger logger;
    private final InvSaveMgr invSaveMgr;
    private final PlayerMessages plmsg;
    private final List<RestoreRequest> restoreRequests;
    public static boolean enabled;

    public OpenGUICommand() {
        plugin = craftiservi.getInstance();
        logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        invSaveMgr = plugin.getInvSaveMgr();
        restoreRequests = plugin.getApprovedRequests();
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
            logger.info("Only Players can run this command!");
            return true;
        }

        if (args.length == 1) {

            String args0 = args[0];
            String[] parts0 = args0.split("\\$");
            String key = parts0[0].trim();
            String[] parts = parts0[1].trim().split(":");
            UUID id0 = UUID.fromString(parts[0].trim());
            String id1 = parts[1].trim();
            RestoreRequest aR = new RestoreRequest(id0, id1);
            if (restoreRequests.contains(aR)) {
                RestoreRequest aR0 = restoreRequests.get(restoreRequests.indexOf(aR));
                if (aR0.isReviewed()) {
                    plmsg.msg(p, ChatColor.RED + "This request has already been reviewed!");
                    return true;
                }
            } else {
                plmsg.msg(p,ChatColor.RED + "This request has already been reviewed!");
                p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                return true;
            }

            if (key.equals("GUI")) {
                if (!invSaveMgr.exists(id0,id1)) {
                    plmsg.msg(p, ChatColor.RED + "Invalid GUI_ID! (GUI$" + parts0[1] + ")");
                    return true;
                }
                Inventory temp = plugin.getInv_saves().get(id0).get(id1);
                Inventory inv = Bukkit.createInventory(temp.getHolder(), 54, Bukkit.getPlayer(id0) == null
                        ? (Bukkit.getOfflinePlayer(id0).getName() == null
                            ? parts0[1]
                            : Objects.requireNonNull(Bukkit.getOfflinePlayer(id0).getName()) + " : " + id1)
                        : Objects.requireNonNull(Bukkit.getPlayer(id0)).getName() + " : " + id1);
                ItemStack[] slots = temp.getContents();
                inv.setContents(slots);


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

                if (approve_meta == null || deny_meta == null || gui_id_meta == null) {return false;}

                approve_lore.add("§7Approve the request and close this inventory");
                deny_lore.add("§7Deny the request and close this inventory");
                gui_id_lore.add("§7ID: " + parts0[1]);

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
            } else {
                sendUsage(p);
            }
        } else {
            sendUsage(p);
        }
        return true;
    }

    private void sendUsage(Player p) {
        plmsg.msg(p,"Usage: /opengui GUI_ID");
    }

}
