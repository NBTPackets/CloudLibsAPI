package github.com.nbtpackets;

import github.com.nbtpackets.menu.InventoryListener;
import github.com.nbtpackets.util.LogUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CloudLibsAPI extends JavaPlugin {

    @Getter
    private static CloudLibsAPI instance;
    private LogUtil log;

    @Override
    public void onEnable() {
        instance = this;
        log = LogUtil.of(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);

        log.info("Автор - NBTPackets / 1.1.0");
        log.info("Github - https://github.com/NBTPackets/CloudLibsAPI");
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}