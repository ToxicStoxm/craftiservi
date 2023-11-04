package com.x_tornado10.events.listeners.inventory;

import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class InventoryListener implements Listener {

    private final craftiservi plugin = craftiservi.getInstance();
    private final Logger logger = plugin.getCustomLogger();
    private final PlayerMessages plmsg = plugin.getPlayerMessages();
    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private boolean cancel = false;
    private HashMap<UUID, String> playerlist = plugin.getPlayerlist();

    private final HashMap<Integer, Inventory> invs_review = plugin.getInvs_review();
    private HashMap<UUID, Integer> penidng_request = new HashMap<>();
    public static boolean enabled;

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent e) {

        if (!enabled) {return;}

        if (isSavedInv(e.getSource()) || isSavedInv(e.getDestination())) {

            e.setCancelled(true);

        }

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {

        if (!enabled) {return;}

        boolean isSaveInv = false;

        for (Map.Entry entry : playerlist.entrySet()) {

            if (e.getView().getTitle().equals(Bukkit.getOfflinePlayer((UUID)entry.getKey()).getName())) {

                isSaveInv = true;

            }

            if (isSaveInv) {break;}

        }

        if (!isSaveInv) {

            return;

        }

        if (e.getClickedInventory() == null || e.getCurrentItem() == null) {

            return;

        }

        Player p = (Player) e.getWhoClicked();

        int slot = e.getSlot();
        ItemStack item = e.getClickedInventory().getItem(slot);
        if (item == null) {return;}
        ItemMeta item_meta = item.getItemMeta();
        if (item_meta == null) {return;}
        String item_displayname = item_meta.getDisplayName();

        String displayname = "§aRestore Inventory";

        if (item_displayname.equals(displayname)) {

            if (!cooldown.containsKey(p.getUniqueId())) {

                cooldown.put(p.getUniqueId(), System.currentTimeMillis());

            } else {

                long timeElapsed = System.currentTimeMillis() - cooldown.get(p.getUniqueId());

                if (!(timeElapsed >= 120000)) {

                    plmsg.msg(p,"§cYou must wait §e120s§c between uses! (" + ((120000 - timeElapsed) / 1000) + "," + ((120000 - timeElapsed) % 1000) + "s left)");
                    p.closeInventory();
                    p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                    e.setCancelled(true);
                    return;

                } else {

                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());

                }

            }

            int id = invs_review.size() + 1;
            invs_review.put(id, e.getInventory());

            plmsg.msg(p,"Sending Inventory restore request...");
            plmsg.msg(p,"Received request. Please wait!");

            if (onlineStaff()) {

                final net.kyori.text.TextComponent inv_restore_request_open_inv = net.kyori.text.TextComponent.builder()
                        .content(plugin.getColorprefix() + plmsg.getLine()  + "\n" + plugin.getColorprefix() + "§7" + p.getName() + " requested Inventory restore!\n" + plugin.getColorprefix())
                        .color(TextColor.GRAY)
                        .append(net.kyori.text.TextComponent.builder("[Inventory]\n")
                                .color(TextColor.AQUA)
                                .hoverEvent(net.kyori.text.event.HoverEvent
                                        .showText(net.kyori.text.TextComponent.builder()
                                                .content("Click to open Inventory! (GUI_" + id + ")")
                                                .color(TextColor.GRAY)
                                                .build()))
                                .clickEvent(net.kyori.text.event.ClickEvent.runCommand("/opengui GUI_" + id)))
                        .append(TextComponent.builder(plugin.getColorprefix() + plmsg.getLine()))
                        .color(TextColor.GRAY)
                        .build();

                for (Player pl : Bukkit.getOnlinePlayers()) {

                    if (pl.isOp()) {

                        TextAdapter.sendComponent(pl, inv_restore_request_open_inv);

                    }

                }

                plmsg.msg(p, "Waiting for approval! (60 sec)");
                penidng_request.put(p.getUniqueId(), id);
                run(id, p);
                p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
                p.closeInventory();

            } else {

                plmsg.msg(p, "§c§lRequest denied! Please try again later!");
                plmsg.msg(p,"Cause: No Operator online!");
                p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                p.closeInventory();
            }

            //restoreInv(p, e.getInventory());

        }


        String approve_displayname = "§aApprove request";
        String deny_displayname = "§cDeny request";

        if (item_displayname.equals(approve_displayname)) {

            cancelrun();

            Inventory temp = e.getInventory();

            List<String> gui_id_lore = temp.getItem(51).getItemMeta().getLore();

            String[] parts = gui_id_lore.get(0).split(":");

            int id = Integer.parseInt(parts[1].trim());

            Player pl = Bukkit.getPlayer(e.getView().getTitle());

            Inventory temp_inv = e.getInventory();

            restoreInv(pl, temp_inv);

            remove_delayed(id);

            p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
            p.closeInventory();

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (p.isOp()) {

                    plmsg.msg_inlines(player,"§e" + p.getName() + " §aapproved §e" + pl.getName() + "'s §aInv request!");

                }

            }

            plmsg.msg_inlines(pl,"§e" + p.getName() + " §aapproved your Inv request!");
            plmsg.msg(pl, "Restoring inventory...");

        }

        if (item_displayname.equals(deny_displayname)) {

            cancelrun();

            Inventory temp = e.getClickedInventory();

            List<String> gui_id_lore = temp.getItem(51).getItemMeta().getLore();

            String[] parts = gui_id_lore.get(0).split(":");

            Player pl = Bukkit.getPlayer(e.getView().getTitle());

            invs_review.remove(Integer.parseInt(parts[1].trim()));

            p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
            p.closeInventory();

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (p.isOp()) {

                    plmsg.msg_inlines(player,"§e" + p.getName() + " §cdenied §e" + pl.getName() + "'s §cInv request!");

                }

            }

            plmsg.msg_inlines(pl,"§e" + p.getName() + " §cdenied your Inv request!");

        }

        e.setCancelled(true);

    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {

        if (!enabled) {return;}

        Player p = e.getPlayer();

        if (penidng_request.containsKey(p.getUniqueId())) {

            invs_review.remove(penidng_request.get(p.getUniqueId()));
            penidng_request.remove(p.getUniqueId());

        }

        for (Player pl : Bukkit.getOnlinePlayers()) {

            if (pl.isOp()) {

                plmsg.msg_inlines(pl, "§cCanceled Inv request from §e" + p.getName() + "\n" + plugin.getColorprefix() + "Cause: §cDisconnected");

            }

        }

    }

    private void remove_delayed(int id) {

        BukkitTask remove_delayed = new BukkitRunnable() {
            @Override
            public void run() {

                invs_review.remove(id);

                cancel();

            }
        }.runTaskTimer(plugin, 10, 20);


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


    private void restoreInv(Player p, Inventory inv) {

        List<ItemStack> armour_slots = new ArrayList<>();
        List<ItemStack> slots = new ArrayList<>();
        ItemStack offhand;

        ItemStack air = new ItemStack(Material.AIR);

        for (int i = 0; i<37; i++) {

            if (inv.getItem(i) == null) {

                slots.add(air);

            } else {

                slots.add(inv.getItem(i));

            }

        }


        for (int j = 36; j<40; j++) {

            if (inv.getItem(j) == null) {

                armour_slots.add(air);

            } else {

                armour_slots.add(inv.getItem(j));

            }

        }

        if (inv.getItem(40) == null) {

            offhand = new ItemStack(Material.AIR);

        } else {

            offhand = inv.getItem(40);

        }

        for (ItemStack slot : p.getInventory()) {

            if (slot != null) {

                p.getWorld().dropItem(p.getLocation(), slot).setPickupDelay(100);

            }

        }

        p.getInventory().clear();

        p.getInventory().setBoots(armour_slots.get(0));
        p.getInventory().setLeggings(armour_slots.get(1));
        p.getInventory().setChestplate(armour_slots.get(2));
        p.getInventory().setHelmet(armour_slots.get(3));

        p.getInventory().setItem(40, offhand);

        for (int l = 0; l<37; l++) {

            p.getInventory().addItem(slots.get(l));

        }
    }

    private boolean onlineStaff() {

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (p.isOp()) {

                return true;

            }

        }

        return false;

    }


    private void run(int id, Player p) {

        BukkitTask run = new BukkitRunnable() {

            @Override
            public void run() {

                invs_review.remove(id);

                for (Player pl : Bukkit.getOnlinePlayers()) {

                    if (pl.isOp()) {

                        plmsg.msg(pl, "§cAutodenied Inv request form " + p.getName());

                    }

                }

                plmsg.msg(p,"Your Inventory restore request was §cauto denied!");
                plmsg.msg(p, "Cause: §cExpired request");
                plmsg.msg(p,"Please try again later!");

                cancel();

            }


        }.runTaskTimer(plugin, 1200, 20);


        BukkitTask cancelrun = new BukkitRunnable() {
            @Override
            public void run() {

                if (cancel) {

                    run.cancel();
                    cancel = false;

                }

            }

        }.runTaskTimer(plugin,0,20);


    }

    private void cancelrun() {

        cancel = true;

    }

}
