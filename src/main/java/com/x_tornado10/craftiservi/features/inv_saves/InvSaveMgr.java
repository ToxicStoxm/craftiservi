package com.x_tornado10.craftiservi.features.inv_saves;

import com.x_tornado10.craftiservi.craftiservi;
import com.x_tornado10.craftiservi.events.custom.ReloadEvent;
import com.x_tornado10.craftiservi.message_sys.OpMessages;
import com.x_tornado10.craftiservi.message_sys.PlayerMessages;
import com.x_tornado10.craftiservi.utils.custom_data.inv_request.RestoreRequest;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.statics.CDID;
import com.x_tornado10.craftiservi.utils.statics.PERMISSION;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class InvSaveMgr implements Listener {

    private final craftiservi plugin;
    private final PlayerMessages plmsg;
    private final OpMessages opmsg;
    private final HashMap<UUID, HashMap<String, Inventory>> inv_saves;
    private double cooldown_value;
    private boolean restore_enabled;
    private final List<RestoreRequest> restoreRequests;
    public static HashMap<UUID, Long> cooldown;
    public InvSaveMgr() {
        plugin = craftiservi.getInstance();
        inv_saves = plugin.getInv_saves();
        plmsg = plugin.getPlayerMessages();
        opmsg = plugin.getOpmsg();
        cooldown = new HashMap<>();
        restoreRequests = plugin.getApprovedRequests();
    }

    public boolean add(UUID pid, String name) {
        if (exists(pid, name)) {return false;}
        Inventory temp = getPlayerInventory(pid);
        if (temp == null) {return false;}
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        Inventory inv = convertInv(p,temp,name);
        if (inv == null) {return false;}
        HashMap<String, Inventory> temp2 = new HashMap<>();
        if (inv_saves.containsKey(pid)) temp2.putAll(inv_saves.get(pid));
        temp2.put(name,inv);
        inv_saves.put(pid,temp2);
        return true;
    }
    public boolean add(UUID pid, HashMap<String,Inventory> inv_save) {
        if (inv_save == null) {return false;}
        inv_saves.put(pid, inv_save);
        return true;
    }
    public boolean remove(UUID pid, String name) {
        if (exists(pid,name)) {
            HashMap<String,Inventory> temp = inv_saves.get(pid);
            restoreRequests.removeIf(rR -> rR.equals(new RestoreRequest(pid, name)));
            return temp.remove(name) != null;
        } else if (name.equals("*")) {
            HashMap<String,Inventory> temp = inv_saves.get(pid);
            if (temp == null) return true;
            restoreRequests.removeIf(rR -> rR.equals(new RestoreRequest(pid, "*")));
            temp.clear();
            return true;
        }
        return false;
    }
    public boolean rename(UUID pid, String name, String new_name) {
        if (!exists(pid, name)) {
            return false;
        }
        RestoreRequest rR = new RestoreRequest(pid,name);
        HashMap<String, Inventory> temp = inv_saves.get(pid);
        Inventory inv = temp.get(name);
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        Inventory inv2 = changeInvName(p, inv, new_name);
        if (inv2 == null) {return false;}
        temp.remove(name);
        temp.put(new_name, inv2);
        if (restoreRequests.contains(rR)) {
            RestoreRequest rR0 = restoreRequests.get(restoreRequests.indexOf(rR));
            rR0.setReviewed(true);
            rR0.setApproved(false);
            restore(rR0);
        }
        return true;
    }
    public boolean view(UUID pid, String name) {
        if (exists(pid,name)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            Inventory temp1 = temp.get(name);
            Inventory inv = Bukkit.createInventory(temp1.getHolder(), 54, Objects.requireNonNull(Bukkit.getPlayer(pid)).getName() + " : " + name);
            ItemStack[] contents = temp1.getContents();
            inv.setContents(contents);
            return openInventory(pid, inv);
        }
        return false;
    }
    public void restoreAll(List<RestoreRequest> restoreRequests) {
        List<RestoreRequest> requests = new ArrayList<>(restoreRequests);
        for (RestoreRequest rR : requests) {
            restore(rR);
        }
        requests.clear();
    }
    public boolean restore(RestoreRequest rR) {
        List<UUID> uuids = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            uuids.add(p.getUniqueId());
        }
        UUID requester = rR.getRequester();
        String inv_name = rR.getInvName();
        if (uuids.contains(requester) && rR.isReviewed()) {
            Player p = Bukkit.getPlayer(requester);
            UUID reviewer = rR.getReviewer();
            if (rR.isApproved()) {
                if (restore(requester, inv_name) && p != null) {
                    String reviewerName;
                    Player pl = Bukkit.getPlayer(reviewer);
                    if (pl != null) reviewerName = pl.getName();
                    else reviewerName = Bukkit.getOfflinePlayer(reviewer).getName();
                    plmsg.msg(p, ChatColor.GREEN + (reviewerName == null ? "An Admin approved your Inventory restore request!" : reviewerName + " approved your Inventory restore request!"));
                    opmsg.send((reviewerName == null ? p.getName() + "s Inventory restore request was approved!" : reviewerName + " approved " + p.getName() + "s Inventory restore request!"));
                    plmsg.msg(p, "Restoring '" + inv_name + "'...");
                    p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
                }
            } else {
                String reviewerName;
                if (reviewer != null) {
                    Player pl = Bukkit.getPlayer(reviewer);
                    if (pl != null) reviewerName = pl.getName();
                    else reviewerName = Bukkit.getOfflinePlayer(reviewer).getName();
                    plmsg.msg(Objects.requireNonNull(p), ChatColor.RED + (reviewerName == null ? "An Admin" : reviewerName) + " denied your restore request!");
                    opmsg.send((reviewerName == null ? p.getName() + "s Inventory restore request was denied!" : reviewerName + " denied " + p.getName() + "s Inventory restore request!"));
                    p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                } else {
                    plmsg.msg(Objects.requireNonNull(p), ChatColor.RED + "Your restore request was auto-denied!");
                    plmsg.msg(Objects.requireNonNull(p), ChatColor.RED + "Probable cause: Renamed InventorySavePoint");
                    opmsg.send(p.getName() + "s Inventory restore request was denied!");
                    p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                }
            }
            restoreRequests.remove(rR);
            return true;
        } else {
            return false;
        }
    }
    public boolean restore(UUID pid, String name) {
        if (exists(pid,name)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            Inventory inv = temp.get(name);
            Player p = Bukkit.getPlayer(pid);
            if (p == null) {return false;}
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
            return true;
        }
        return false;
    }

    public boolean requestRestore(UUID pid, String name) {
        if (!exists(pid,name)) {return false;}
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        if (!restore_enabled) {
            plmsg.msg(p,"Failed to send restore request. The restore feature is currently disabled.");
            p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
            return true;
        }
        if (opmsg.isAdmin(pid)) {
            plmsg.msg(p,"Successfully requested to restore " + name + "!");
            p.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    RestoreRequest rR = new RestoreRequest(pid,name);
                    if (restoreRequests.contains(rR)) {
                        RestoreRequest rR0 = restoreRequests.get(restoreRequests.indexOf(rR));
                        restoreRequests.remove(rR0);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            restore(pid,name);
                            p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
                            plmsg.msg(p,"§aAuto approved your restore request because of admin privileges!");
                            plmsg.msg(p,"Restoring " + name + "...");
                        }
                    }.runTask(plugin);
                }
            }.runTaskLaterAsynchronously(plugin,10);
            return true;
        }

        if (!cooldown.containsKey(pid)|| plugin.hasPermission(p, PERMISSION.COMMAND_INVSAVE_BYPASSCOOLDOWN)) {
            cooldown.put(pid, System.currentTimeMillis());
        } else {
            long timeElapsed = System.currentTimeMillis() - cooldown.get(pid);
            if (timeElapsed >= cooldown_value) {
                cooldown.put(pid, System.currentTimeMillis());
            } else {
                plmsg.msg(p,"§cYou must wait §e"+ cooldown_value/1000 +"s§c between uses! (" + (int)((cooldown_value - timeElapsed) / 1000) + "," + (int)((cooldown_value - timeElapsed) % 1000) + "s left)");
                p.closeInventory();
                p.playSound(p, Sound.BLOCK_ANVIL_PLACE, 99999999999999999999999999999999999999f, 1f);
                return false;
            }
        }
        opmsg.send(plmsg.getLine(), pid, name);
        p.closeInventory();
        p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 99999999999999999999999999999999999999f, 1f);
        plmsg.msg(p,"Successfully requested to restore " + name + "!");
        plmsg.msg(p,"Please wait for an admin to review your request!");
        return true;
    }
    private boolean openInventory(UUID pid, String name) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        Inventory inv = null;
        if (exists(pid,name)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            inv = temp.get(name);
        }
        if (inv == null) {return false;}
        p.openInventory(inv);
        return true;
    }
    private boolean openInventory(UUID pid, Inventory inv) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        p.openInventory(inv);
        return true;
    }

    private Inventory getPlayerInventory(UUID pid) {
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return null;}
        return p.getInventory();
    }
    private Inventory convertInv(Player holder, Inventory inv, String name) {
        if (inv == null) {return null;}

        Inventory temp = Bukkit.createInventory(holder, 54, holder.getUniqueId() + " : " + name);
        ItemStack[] slots = inv.getContents();
        temp.setContents(slots);

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta info_meta = info.getItemMeta();
        List<String> info_lore = new ArrayList<>();

        Date date1 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        double x = holder.getLocation().getX();
        double y = holder.getLocation().getY();
        double z = holder.getLocation().getZ();
        double yaw = holder.getLocation().getYaw();
        double pitch = holder.getLocation().getPitch();

        DecimalFormat df = new DecimalFormat("#.##");

        if (info_meta == null) {return null;}

        info_meta.setDisplayName("§9§lInventorySavePoint Info v1.0");
        info_lore.add("§8§lCreation Date:§r§7 " + sdf.format(date1));
        info_lore.add("§8§lLocation:§r§7 x[" + df.format(x) + "] y[" + df.format(y) + "] z[" + df.format(z) + "] yaw[" + df.format(yaw) + "] pitch[" + df.format(pitch) + "]");
        info_lore.add("§8§lWorld:§r§7 " + holder.getWorld().getName());
        info_lore.add("§8§lXp-Level:§r§7 " + holder.getLevel());
        info_lore.add("§8§lHealth:§r§7 " + holder.getHealth());
        info_lore.add("§8§lGamemode:§r§7 " + holder.getGameMode());
        info_meta.setLore(info_lore);
        info.setItemMeta(info_meta);

        temp.setItem(45, info);
        if (restore_enabled) {
            ItemStack restore = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta restore_meta = restore.getItemMeta();
            if (restore_meta == null) {
                return null;
            }
            restore_meta.setDisplayName("§7Restore Inventory");

            List<String> restore_lore = new ArrayList<>();
            restore_lore.add("§7Restores the saved Inventory and drops the items of your current inventory!");
            restore_meta.setLore(restore_lore);
            restore.setItemMeta(restore_meta);

            temp.setItem(53, restore);
        }

        ItemStack gui_id = new ItemStack(Material.BOOK);
        ItemMeta gui_id_meta = gui_id.getItemMeta();
        String gui_id_dispalyname = "§bGUI_ID";
        List<String> gui_id_lore = new ArrayList<>();

        if (gui_id_meta == null) return null;

        gui_id_lore.add("§7ID: " + holder.getUniqueId() + ":" + name);
        gui_id_meta.setDisplayName(gui_id_dispalyname);
        gui_id_meta.setLore(gui_id_lore);
        gui_id.setItemMeta(gui_id_meta);
        temp.setItem(51, gui_id);

        return temp;

    }

    public boolean exists(UUID pid, String name) {
        if (inv_saves.containsKey(pid)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            return temp.containsKey(name);
        }
        return false;
    }

    private Inventory changeInvName(Player holder, Inventory inv, String name) {
        if (inv == null) {return null;}
        Inventory temp = Bukkit.createInventory(holder, 54, holder.getUniqueId() + " : " + name);
        ItemStack[] slots = inv.getContents();
        temp.setContents(slots);
        return temp;
    }
    public ArrayList<String> getPlayerInvSaves(UUID pid) {
        ArrayList<String> result = new ArrayList<>();
        if (!inv_saves.containsKey(pid)) {return new ArrayList<>();}
        for (Map.Entry<String, Inventory> entry : inv_saves.get(pid).entrySet()) {
            result.add(entry.getKey());
        }
        return result;
    }

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData inv_data = e.getData(CDID.INV_DATA);
        cooldown_value = inv_data.getD(0);
        restore_enabled = inv_data.getB(0);
    }
}
