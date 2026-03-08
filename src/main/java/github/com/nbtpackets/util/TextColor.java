package github.com.nbtpackets.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextColor {
    private final MiniMessage MM = MiniMessage.miniMessage();
    private final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public Component parse(String msg) {
        return parse(msg, null);
    }

    public Component parse(String msg, Map<String, String> placeholders) {
        if (msg == null || msg.isEmpty()) return Component.empty();

        if (placeholders != null && !placeholders.isEmpty()) {
            for (var entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        msg = replaceHex(msg);

        if (msg.contains("&")) {
            msg = MM.serialize(LEGACY.deserialize(msg));
        }

        return MM.deserialize(msg);
    }

    private String replaceHex(String msg) {
        Matcher matcher = HEX_PATTERN.matcher(msg);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<color:#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}