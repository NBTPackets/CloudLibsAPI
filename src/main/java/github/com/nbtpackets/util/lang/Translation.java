package github.com.nbtpackets.util.lang;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import github.com.nbtpackets.util.LogUtil;
import github.com.nbtpackets.util.TextColor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Translation {

    private final Plugin plugin;
    private final LogUtil log;
    private final Map<String, String> messages = new ConcurrentHashMap<>();

    public Translation(Plugin plugin) {
        this.plugin = plugin;
        this.log = LogUtil.of(plugin);
    }

    public void load(String activeLang, List<String> supportedLangs) {
        messages.clear();

        File folder = new File(plugin.getDataFolder(), "lang");
        if (!folder.exists()) folder.mkdirs();

        supportedLangs.forEach(this::unpackResource);
        loadActiveLanguage(activeLang);
    }

    private void unpackResource(String langName) {
        String path = "lang/" + langName + ".yml";
        File file = new File(plugin.getDataFolder(), path);

        if (!file.exists()) {
            Optional.ofNullable(plugin.getResource(path)).ifPresentOrElse(
                    is -> {
                        plugin.saveResource(path, false);
                        log.info("на всякий файл {} будет выгружен из джарника", langName);
                    },
                    () -> {
                        try {
                            if (file.createNewFile()) {
                                log.warn("файл {}.yml в джарнике нет, так что будет создан пустой на всякий", langName);
                            }
                        } catch (IOException ignored) {}
                    }
            );
        }
    }

    private void loadActiveLanguage(String langName) {
        File file = new File(plugin.getDataFolder(), "lang/" + langName + ".yml");

        if (!file.exists()) {
            log.error("файла {}.yml вообще не существует, даже пустого", langName);
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.getKeys(true).stream()
                .filter(key -> config.isString(key) || config.isList(key))
                .forEach(key -> messages.put(key, parseValue(config.get(key))));

        log.info("загрузилось локализацию {} ({} настроек)", langName, messages.size());
    }

    private String parseValue(Object v) {
        return (v instanceof List<?> list)
                ? list.stream().map(Object::toString).collect(Collectors.joining("\n"))
                : String.valueOf(v);
    }

    public Optional<String> find(String key) {
        return Optional.ofNullable(messages.get(key));
    }

    public Component get(String key) {
        return get(key, Map.of());
    }

    public Component get(String key, Map<String, String> placeholders) {
        return find(key)
                .map(raw -> TextColor.parse(raw, placeholders))
                .orElseGet(() -> TextColor.parse("<red>сообщение:" + key + " нету"));
    }
}