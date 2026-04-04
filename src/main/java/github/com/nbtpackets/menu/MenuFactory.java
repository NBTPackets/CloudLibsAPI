package github.com.nbtpackets.menu;

import org.bukkit.entity.Player;
import java.util.function.Supplier;

public final class MenuFactory {

    private MenuFactory() {}

    public static void open(Player player, Supplier<? extends BaseMenu> menuSupplier) {
        BaseMenu menu = menuSupplier.get();
        player.openInventory(menu.getInventory());
    }
}