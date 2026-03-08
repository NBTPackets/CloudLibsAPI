package github.com.nbtpackets.player;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class PlayerInv {

    public void clear(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setExtraContents(null);
        player.getInventory().setItemInOffHand(null);
    }

    public boolean hasSpace(Player player) {
        return player.getInventory().firstEmpty() != -1;
    }

    public void give(Player player, ItemStack item) {
        var added = player.getInventory().addItem(item);
        if (!added.isEmpty()) {
            added.values().forEach(remain ->
                    player.getWorld().dropItem(player.getLocation(), remain));
        }
    }

    public boolean hasItem(Player player, Material material) {
        return player.getInventory().contains(material);
    }
}