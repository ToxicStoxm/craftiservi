package com.x_tornado10.events.listeners.inventory;

import com.x_tornado10.commands.inv_save_point.InventorySavePointCommand;
import com.x_tornado10.craftiservi;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryOpenListener implements Listener {

    private final craftiservi plugin = craftiservi.getInstance();
    private final Logger logger = plugin.getCustomLogger();
    private final PlayerMessages plmsg = plugin.getPlayerMessages();
    public static boolean enabled;

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {

        if (!enabled) {return;}

        Player p = (Player) e.getPlayer();
        Inventory i = e.getInventory();
        String display_name = "§9§lInventorySavePoint Info v1.0";

        if (i.getSize() != 54) {return;}

        ItemStack item = i.getItem(45);
        assert item != null;
        ItemMeta item_meta = item.getItemMeta();
        assert item_meta != null;
        String item_displayname = item_meta.getDisplayName();

        if (item_displayname.equals(display_name)) {

            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 99999999999999999999999999999999999999f, 1);

        }

    }

}
