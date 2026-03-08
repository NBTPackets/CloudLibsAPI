package github.com.nbtpackets.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import github.com.nbtpackets.util.TextColor;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;
    private final Map<String, String> placeholders = new HashMap<>();

    public static ItemBuilder create(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder create(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder create(ItemStack base) {
        return new ItemBuilder(base.clone());
    }

    public static ItemStack fromMap(Map<?, ?> map, Map<String, String> placeholders) {
        if (map == null || map.isEmpty()) return null;

        String matName = Optional.ofNullable(map.get("material"))
                .map(Object::toString)
                .orElse(null);

        if (matName == null) {
            Bukkit.getLogger().warning("Материал не указан в конфиге");
            return null;
        }

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                matName = matName.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        Material material = Material.matchMaterial(matName.toUpperCase());
        if (material == null) {
            Bukkit.getLogger().severe("Материал '" + matName + "' не найден");
            return null;
        }

        int amount = Optional.ofNullable(map.get("amount"))
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .map(Number::intValue)
                .orElse(1);

        ItemBuilder builder = new ItemBuilder(material, amount);

        if (placeholders != null) {
            builder.placeholders.putAll(placeholders);
        }

        Optional.ofNullable(map.get("name"))
                .map(Object::toString)
                .ifPresent(builder::name);

        Object loreObj = map.get("lore");
        if (loreObj instanceof List<?> list) {
            builder.lore(list.stream().map(Object::toString).toList());
        }

        Optional.ofNullable(map.get("custom-model-data"))
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .map(Number::intValue)
                .ifPresent(builder::customModelData);

        if (Boolean.TRUE.equals(map.get("unbreakable"))) {
            builder.unbreakable(true);
        }

        Optional.ofNullable(map.get("enchantments"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .ifPresent(enchMap -> enchMap.forEach((k, v) -> {
                    if (v instanceof Number level) {
                        String keyStr = k.toString().toLowerCase();
                        NamespacedKey key = keyStr.contains(":")
                                ? NamespacedKey.fromString(keyStr)
                                : NamespacedKey.minecraft(keyStr);

                        Enchantment ench = Enchantment.getByKey(key);
                        if (ench != null) builder.enchant(ench, level.intValue());
                        else Bukkit.getLogger().warning("Зачарование '" + keyStr + "' не найдено");
                    }
                }));

        Optional.ofNullable(map.get("hide-flags")).ifPresent(flags -> {
            if (flags instanceof String str && "all".equalsIgnoreCase(str)) {
                builder.hideAllFlags();
            } else if (flags instanceof List<?> list) {
                list.forEach(f -> {
                    try { builder.hideFlags(ItemFlag.valueOf(f.toString().toUpperCase())); }
                    catch (Exception ignored) {}
                });
            }
        });

        return builder.build();
    }

    private ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = Objects.requireNonNull(stack.getItemMeta());
    }

    private ItemBuilder(Material material, int amount) {
        this.stack = new ItemStack(material, amount);
        this.meta = Objects.requireNonNull(stack.getItemMeta());
    }

    private ItemBuilder(ItemStack stack) {
        this.stack = stack;
        this.meta = Objects.requireNonNull(stack.getItemMeta());
    }

    public ItemBuilder name(String name) {
        Optional.ofNullable(name).ifPresent(s -> {
            Component parsed = TextColor.parse(s, placeholders)
                    .decoration(TextDecoration.ITALIC, false);
            meta.displayName(parsed);
        });
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            meta.lore(Collections.emptyList());
            return this;
        }

        List<Component> components = lore.stream()
                .map(line -> TextColor.parse(line, placeholders)
                        .decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList());

        meta.lore(components);
        return this;
    }

    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder hideAllFlags() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder hideFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder customModelData(Integer data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }
}