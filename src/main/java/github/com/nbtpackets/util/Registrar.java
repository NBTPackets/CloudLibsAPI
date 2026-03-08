package github.com.nbtpackets.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

@UtilityClass
public class Registrar {

    public void events(Plugin plugin, Listener... listeners) {
        Arrays.stream(listeners)
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
    }
}