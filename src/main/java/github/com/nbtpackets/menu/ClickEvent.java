package github.com.nbtpackets.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public record ClickEvent(Player player,
                         int slot,
                         ItemStack clickedItem,
                         ItemStack cursorItem,
                         ClickType clickType,
                         InventoryClickEvent event) {

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSound(Sound sound) {
        playSound(sound, 1.0f, 1.0f);
    }
}