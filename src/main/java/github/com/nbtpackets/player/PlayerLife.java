package github.com.nbtpackets.player;

import lombok.experimental.UtilityClass;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

@UtilityClass
public class PlayerLife {

    public void heal(Player player) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(maxHealth);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public void feed(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20f);
    }

    public void restore(Player player) {
        heal(player);
        feed(player);
    }

    public void gm(Player player, GameMode mode) {
        player.setGameMode(mode);
    }
}