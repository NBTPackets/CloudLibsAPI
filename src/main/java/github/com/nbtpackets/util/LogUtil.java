package github.com.nbtpackets.util;

import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

public class LogUtil {

    private final Logger logger;

    private LogUtil(Logger logger) {
        this.logger = logger;
    }

    public static LogUtil of(Plugin plugin) {
        return new LogUtil(plugin.getSLF4JLogger());
    }

    public void info(String msg, Object... args) {
        logger.info(msg, args);
    }

    public void warn(String msg, Object... args) {
        logger.warn(msg, args);
    }

    public void error(String msg, Object... args) {
        logger.error(msg, args);
    }
}