package com.x_tornado10.features.inv_saves;

import com.x_tornado10.craftiservi;
import com.x_tornado10.events.custom.ReloadEvent;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileCount;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class InvSaveMgr implements Listener {

    private craftiservi plugin;
    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;
    public InvSaveMgr() {
        plugin = craftiservi.getInstance();
        inv_saves = plugin.getInv_saves();
    }

    public boolean add(UUID pid, String name) {
        if (exists(pid, name)) {return false;}
        Inventory temp = getPlayerInventory(pid);
        if (temp == null) {return false;}
        Player p = Bukkit.getPlayer(pid);
        if (p == null) {return false;}
        Inventory inv = convertInv(p,temp,name);
        if (inv == null) {return false;}
        HashMap<String,Inventory> temp2 = new HashMap<>();
        temp2.put(name,inv);
        inv_saves.put(pid, temp2);
        return true;
    }
    public boolean add(UUID pid, HashMap<String,Inventory> inv_save) {
        if (inv_save == null) {return false;}
        inv_saves.put(pid, inv_save);
        return true;
    }
    public boolean remove(UUID pid, String name) {
        if (inv_saves.containsKey(pid)) {
            HashMap<String,Inventory> temp = inv_saves.get(pid);
            return temp.remove(name) != null;
        } else return false;
    }
    public boolean rename(UUID pid, String name, String new_name) {
        if (!exists(pid, name)) {return false;}
            HashMap<String,Inventory> temp = inv_saves.get(pid);
            Inventory inv = temp.get(name);
            Player p = Bukkit.getPlayer(pid);
            if (p == null) {return false;}
            Inventory inv2 = changeInvName(p,inv,name);
            if (inv2 == null) {return false;}
            temp.remove(name);
            temp.put(new_name, inv2);
            return remove(pid,name) && add(pid,temp);
    }
    public boolean view(UUID pid, String name) {
        if (exists(pid,name)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            Inventory inv = temp.get(name);
            return openInventory(pid, name, inv);
        }
        return false;
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
    private boolean openInventory(UUID pid, String name, Inventory inv) {
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

        Inventory temp = Bukkit.createInventory(holder, 54, holder.getName() + " : " + name);
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

        ItemStack restore = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta restore_meta = restore.getItemMeta();
        if (restore_meta == null) {return null;}
        restore_meta.setDisplayName("§aRestore Inventory");

        List<String> restore_lore = new ArrayList<>();
        restore_lore.add("§7Restores the saved Inventory and drops the items of your current inventory!");
        restore_meta.setLore(restore_lore);
        restore.setItemMeta(restore_meta);

        temp.setItem(53,restore);

        return temp;

    }

    private boolean exists(UUID pid, String name) {
        if (inv_saves.containsKey(pid)) {
            HashMap<String, Inventory> temp = inv_saves.get(pid);
            return temp.containsKey(name);
        }
        return false;
    }

    private Inventory changeInvName(Player holder, Inventory inv, String name) {
        if (inv == null) {return null;}
        Inventory temp = Bukkit.createInventory(holder, 54, holder.getName() + " : " + name);
        ItemStack[] slots = inv.getContents();
        temp.setContents(slots);
        return temp;
    }


    @EventHandler
    public void onReload(ReloadEvent e) {

    }
}
