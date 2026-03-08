package github.com.nbtpackets;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import github.com.nbtpackets.util.LogUtil;

public final class CloudLibAPI extends JavaPlugin {
    
    @Getter private static CloudLibAPI instance;
    private LogUtil log;

    @Override
    public void onEnable() {
        instance = this;
        log = LogUtil.of(this);
        log.info("Автор - NBTPackets");
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
