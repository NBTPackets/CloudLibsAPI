package github.com.nbtpackets.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryHolder inventoryHolder = player.getOpenInventory().getTopInventory().getHolder();
        if (!isBaseMenu(inventoryHolder)) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType().isAir()) {
            return;
        }

        BaseMenu baseMenu = getBaseMenu(inventoryHolder);
        event.setCancelled(true);
        baseMenu.onClick(event);
        baseMenu.handleItemClick(new ClickEvent(
                player,
                event.getRawSlot(),
                currentItem,
                event.getCursor(),
                event.getClick(),
                event));
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (isBaseMenu(inventoryHolder)) {
            BaseMenu baseMenu = getBaseMenu(inventoryHolder);
            baseMenu.onClose(event);
        }
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event) {
        if (isBaseMenu(event.getInventory().getHolder())) {
            event.setCancelled(true);
        }
    }

    private BaseMenu getBaseMenu(InventoryHolder inventoryHolder) {
        if (inventoryHolder == null) return null;

        Inventory inventory = inventoryHolder.getInventory();
        if (inventory == null) return null;

        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof BaseMenu menu) {
            return menu;
        }

        return null;
    }

    private boolean isBaseMenu(InventoryHolder inventoryHolder) {
        if (inventoryHolder == null) return false;

        Inventory inventory = inventoryHolder.getInventory();
        if (inventory == null) return false;

        return inventory.getHolder() instanceof BaseMenu;
    }
}