package github.com.nbtpackets.player;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import github.com.nbtpackets.util.TextColor;
import github.com.nbtpackets.util.lang.Translation;

import java.util.Map;

@UtilityClass
public class PlayerMsg {

    public void send(Player player, String msg) {
        player.sendMessage(TextColor.parse(msg));
    }

    public void send(Player player, String msg, Map<String, String> replacements) {
        player.sendMessage(TextColor.parse(msg, replacements));
    }

    public void lang(Player player, Translation lang, String key) {
        player.sendMessage(lang.get(key));
    }

    public void lang(Player player, Translation lang, String key, Map<String, String> replacements) {
        player.sendMessage(lang.get(key, replacements));
    }

    public void action(Player player, String msg) {
        player.sendActionBar(TextColor.parse(msg));
    }

    public void title(Player player, String title, String subtitle) {
        var t = TextColor.parse(title);
        var s = TextColor.parse(subtitle);
        player.showTitle(Title.title(t, s));
    }
}