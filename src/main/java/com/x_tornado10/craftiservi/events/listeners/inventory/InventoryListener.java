package com.x_tornado10.craftiservi.events.listeners.inventory;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.features.inv_saves.InvSaveMgr;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.inv_request.RestoreRequest;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import com.x_tornado10.craftiservi.utils.statics.PLACEHOLDER;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventoryListener implements Listener {

    private final craftiservi plugin;
    private final PlayerMessages plmsg;
    private boolean autoInvReqOnDeath;
    private String autoInvReqOnDeath_format;
    private final List<RestoreRequest> restoreRequests;
    private final InvSaveMgr invSaveMgr;
    public static boolean enabled;

    public InventoryListener() {
        plugin = craftiservi.getInstance();
        //logger = plugin.getCustomLogger();
        plmsg = plugin.getPlayerMessages();
        invSaveMgr = plugin.getInvSaveMgr();
        restoreRequests = plugin.getApprovedRequests();
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent e) {
        if (!enabled) {return;}
        if (isSavedInv(e.getSource()) || isSavedInv(e.getDestination())) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!enabled || e.getClickedInventory() == null || e.getCurrentItem() == null) {return;}
        try {
            if (!isSavedInv(e.getView())) {return;}
        } catch (IllegalArgumentException ex) {
            if (!isSavedInv(e.getClickedInventory()) && !isSavedInv(e.getInventory())) {return;}
        }

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        UUID pid = p.getUniqueId();

        int slot = e.getSlot();
        ItemStack item = e.getClickedInventory().getItem(slot);
        if (item == null) {return;}
        ItemMeta item_meta = item.getItemMeta();
        if (item_meta == null) {return;}
        String item_displayname = item_meta.getDisplayName();

        String[] parts = e.getView().getTitle().split(":");

        String displayname = "§7Restore Inventory";
        String approve_displayname = "§aApprove request";
        String deny_displayname = "§cDeny request";

        //logger.severe("InvClickEvent");

        if (item_displayname.equals(displayname)) {
            //logger.severe("InvClickEvent -> restore button");
            UUID uuid = getUUID(e.getClickedInventory(),e.getInventory(),parts,p);
            if (uuid == null) {return;}
            //logger.severe("InvClickEvent -> restore button - 1");
            RestoreRequest rR = new RestoreRequest(uuid, parts[1].trim());
            if (restoreRequests.contains(rR)) {
                //logger.severe("InvClickEvent -> restore button - already exists");
                plmsg.msg(p,ChatColor.RED + "Failed to send request! You already have another pending request!");
                p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                p.closeInventory();
                return;
            }
            if (invSaveMgr.requestRestore(pid, parts[1].trim())) {
                restoreRequests.add(rR);
            }
            //logger.severe("InvClickEvent -> restore button - restore req send");
            return;
        }
        if (item_displayname.equals(approve_displayname)) {
            //logger.severe("InvClickEvent -> approve button");
            UUID uuid = getUUID(e.getClickedInventory(),e.getInventory(),parts,p);
            if (uuid == null) {return;}
            //logger.severe("InvClickEvent -> approve button - 1");
            RestoreRequest rR = new RestoreRequest(uuid, parts[1].trim());
            if (restoreRequests.contains(rR)) {
                RestoreRequest rR0 = restoreRequests.get(restoreRequests.indexOf(rR));
                //logger.severe("InvClickEvent -> approve button - restoreRequest contains");
                if (rR0.isReviewed()) {
                    //logger.severe("InvClickEvent -> approve button - restoreRequest contains - is reviewed");
                    plmsg.msg(p,"This request has already been reviewed!");
                    p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                    p.closeInventory();
                    return;
                } else {
                    //logger.severe("InvClickEvent -> approve button - restoreRequest contains - !is reviewed");
                    rR0.setApproved(true);
                    rR0.setReviewed(true);
                    rR0.setReviewer(pid);
                }
            } else {
                //logger.severe("InvClickEvent -> approve button - restoreRequest !contains");
                rR.setReviewed(true);
                rR.setApproved(true);
                rR.setReviewer(pid);
                restoreRequests.add(rR);
            }
            RestoreRequest rR_final = restoreRequests.get(restoreRequests.indexOf(rR));
            invSaveMgr.restore(rR_final);
            p.closeInventory();
            p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
            //logger.severe("InvClickEvent -> approve button - ... - complete");
            return;
        }
        if (item_displayname.equals(deny_displayname)) {
            //logger.severe("InvClickEvent -> deny button");
            UUID uuid = getUUID(e.getClickedInventory(),e.getInventory(),parts,p);
            if (uuid == null) {return;}
            //logger.severe("InvClickEvent -> deny button - 1");
            RestoreRequest rR = new RestoreRequest(uuid, parts[1].trim());
            if (restoreRequests.contains(rR)) {
                //logger.severe("InvClickEvent -> deny button -  restoreRequest contains");
                RestoreRequest rR0 = restoreRequests.get(restoreRequests.indexOf(rR));
                if (rR0.isReviewed()) {
                    //logger.severe("InvClickEvent -> deny button -  restoreRequest contains - is reviewed");
                    plmsg.msg(p,"This request has already been reviewed!");
                    p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                    p.closeInventory();
                    return;
                } else {
                    //logger.severe("InvClickEvent -> deny button -  restoreRequest contains - !is reviewed");
                    rR0.setReviewed(true);
                    rR0.setApproved(false);
                    rR0.setReviewer(pid);
                }
            } else {
                //logger.severe("InvClickEvent -> deny button -  restoreRequest !contains");
                rR.setReviewed(true);
                rR.setApproved(false);
                rR.setReviewer(pid);
                restoreRequests.add(restoreRequests.get(restoreRequests.indexOf(rR)));
            }
            RestoreRequest rR_final = restoreRequests.get(restoreRequests.indexOf(rR));
            invSaveMgr.restore(rR_final);
            p.closeInventory();
            p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
            //logger.severe("InvClickEvent -> deny button -  ... - complete");
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!enabled) return;
        invSaveMgr.restoreAll(restoreRequests);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {

        if (!enabled) {return;}

        Player p = (Player) e.getPlayer();
        Inventory i = e.getInventory();
        String display_name = "§9§lInventorySavePoint Info v1.0";

        if (i.getSize() != 54) {return;}

        ItemStack item = i.getItem(45);
        if (item == null) {return;}
        ItemMeta item_meta = item.getItemMeta();
        if (item_meta == null) {return;}
        String item_displayname = item_meta.getDisplayName();

        if (item_displayname.equals(display_name)) {

            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 99999999999999999999999999999999999999f, 1);

        }

    }

    private boolean isSavedInv(Inventory inv, Player p) throws IllegalArgumentException{

        boolean saveToCheck = false;
        int index = 0;

        for (HumanEntity humanEntity : inv.getViewers()) {
            if (humanEntity.getName().equals(p.getName())) {
                saveToCheck = true;
                index = inv.getViewers().indexOf(humanEntity);
            }
        }

        if (!saveToCheck) {return false;}
        String title = inv.getViewers().get(index).getOpenInventory().getTitle();
        String[] parts = title.split(":");
        return invSaveMgr.exists(UUID.fromString(parts[0].trim()), parts[1].trim());
    }
    private boolean isSavedInv(InventoryView inventoryView) throws IllegalArgumentException{
        String title = inventoryView.getTitle();
        String[] parts = title.split(":");
        if (parts.length != 2) return false;
        return invSaveMgr.exists(UUID.fromString(parts[0].trim()), parts[1].trim());
    }

    private boolean isSavedInv(Inventory inv) {

        String display_name = "§9§lInventorySavePoint Info v1.0";
        if (inv.getSize() != 54) {return false;}

        ItemStack item = inv.getItem(45);
        if (item == null) {return false;}
        ItemMeta item_meta = item.getItemMeta();
        if (item_meta == null) {return false;}
        String item_displayname = item_meta.getDisplayName();

        return item_displayname.equals(display_name);

    }

    private UUID getUUID(Inventory clickedInv, Inventory inv0, String[] parts, Player p) {
        UUID uuid;
        try {
            uuid = UUID.fromString(parts[0].trim());
        } catch (IllegalArgumentException ex) {
            Inventory inv = null;
            if (clickedInv.getSize() != 54) {
                if (inv0.getSize() != 54) {
                    p.closeInventory();
                    plmsg.msg(p,"ERROR OCCURRED!");
                    return null;
                }
                inv = inv0;
            }
            if (inv == null) inv = clickedInv;
            ItemStack item0 = inv.getItem(51);
            if (item0 == null) return null;
            ItemMeta item0_meta = item0.getItemMeta();
            if (item0_meta == null) return null;
            List<String> item0_lore = item0_meta.getLore();
            if (item0_lore == null || item0_lore.get(0) == null) return null;
            uuid = UUID.fromString(item0_lore.get(0).split(":")[1].trim());
        }
        return uuid;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (autoInvReqOnDeath && enabled) {
            Player p = e.getEntity();
            invSaveMgr.add(p.getUniqueId(), formatInvName(autoInvReqOnDeath_format,p));
        }
    }

    private String formatInvName(@NotNull String inv_name, @NotNull Player p) {
        String result = inv_name;
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (inv_name.contains(PLACEHOLDER.DATE)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            String formattedDate = currentDateTime.format(dateFormatter);
            result = result.replace(PLACEHOLDER.DATE, formattedDate);
        }
        if (inv_name.contains(PLACEHOLDER.TIME)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH-mm-ss");
            String formattedDate = currentDateTime.format(dateFormatter);
            result = result.replace(PLACEHOLDER.TIME, formattedDate);

        }
        if (inv_name.contains(PLACEHOLDER.COORDINATES)) {
            String formattedCoordinates = getString(p);
            result = result.replace(PLACEHOLDER.COORDINATES, formattedCoordinates);
        }
        if (inv_name.contains(PLACEHOLDER.WORLD)) {
            World world = p.getWorld();
            String world_name = world.getName();
            result = result.replace(PLACEHOLDER.WORLD, world_name);
        }
        result = result.replace(" ", "");
        result = result.replace(".", "");
        return result;
    }

    @NotNull
    private static String getString(@NotNull Player p) {
        Location loc = p.getLocation();
        double X = loc.getX();
        double Y = loc.getY();
        double Z = loc.getZ();
        X = (double) Math.round(X * 100.0) /100.0;
        Y = (double) Math.round(Y * 100.0) /100.0;
        Z = (double) Math.round(Z * 100.0) /100.0;
        String sX = String.valueOf(X).replace(".",",");
        String sY = String.valueOf(Y).replace(".",",");
        String sZ = String.valueOf(Z).replace(".",",");
        return "X:" + sX + "/-Y:" + sY + "/-Z:" + sZ;
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData invData = e.getData(CDID.INV_DATA);
        autoInvReqOnDeath = invData.getB(1);
        autoInvReqOnDeath_format = invData.getS(0);
    }
}
