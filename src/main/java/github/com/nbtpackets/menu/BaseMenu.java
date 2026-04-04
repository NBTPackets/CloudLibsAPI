package github.com.nbtpackets.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import github.com.nbtpackets.util.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BaseMenu implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, Consumer<ClickEvent>> clickActions = new HashMap<>();
    private final Map<Integer, Supplier<ItemStack>> dynamicItems = new HashMap<>();
    private final Map<Integer, Integer> animationTasks = new HashMap<>();
    private int updateTaskId = -1;
    private boolean closed = false;
    private Player viewer;

    private Sound openSound = null;
    private float openVolume = 1.0f;
    private float openPitch = 1.0f;

    public BaseMenu(int size, Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void setItem(int slot, ItemStack item, Consumer<ClickEvent> action) {
        this.clickActions.put(slot, action);
        this.inventory.setItem(slot, item);
    }

    public void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    public void setDynamicItem(int slot, Supplier<ItemStack> itemSupplier, Consumer<ClickEvent> action) {
        this.clickActions.put(slot, action);
        this.dynamicItems.put(slot, itemSupplier);
        updateDynamicSlot(slot);
    }

    public void setDynamicItem(int slot, Supplier<ItemStack> itemSupplier) {
        this.dynamicItems.put(slot, itemSupplier);
        updateDynamicSlot(slot);
    }

    private void updateDynamicSlot(int slot) {
        if (dynamicItems.containsKey(slot)) {
            ItemStack newItem = dynamicItems.get(slot).get();
            inventory.setItem(slot, newItem);
        }
    }

    public void refresh() {
        dynamicItems.keySet().forEach(this::updateDynamicSlot);
    }

    public void startAnimation(int slot, ItemStack[] frames, long delayBetweenTicks, int repeatCount) {
        stopAnimation(slot);

        int[] currentFrame = {0};
        int[] repeats = {0};

        int taskId = Task.timer(0L, delayBetweenTicks, () -> {
            if (closed) {
                stopAnimation(slot);
                return;
            }

            inventory.setItem(slot, frames[currentFrame[0]]);
            currentFrame[0]++;

            if (currentFrame[0] >= frames.length) {
                currentFrame[0] = 0;
                repeats[0]++;

                if (repeatCount > 0 && repeats[0] >= repeatCount) {
                    stopAnimation(slot);
                }
            }
        }).getTaskId();

        animationTasks.put(slot, taskId);
    }

    public void startInfiniteAnimation(int slot, ItemStack[] frames, long delayBetweenTicks) {
        startAnimation(slot, frames, delayBetweenTicks, -1);
    }

    public void stopAnimation(int slot) {
        if (animationTasks.containsKey(slot)) {
            Bukkit.getScheduler().cancelTask(animationTasks.get(slot));
            animationTasks.remove(slot);
        }
    }

    public void stopAllAnimations() {
        animationTasks.keySet().forEach(this::stopAnimation);
    }

    public void startAutoUpdate(long periodTicks) {
        if (updateTaskId != -1) stopAutoUpdate();

        updateTaskId = Task.timer(0L, periodTicks, () -> {
            if (!closed) {
                onUpdate();
                refresh();
            }
        }).getTaskId();
    }

    public void startAutoUpdate(long periodTicks, Runnable updateLogic) {
        if (updateTaskId != -1) stopAutoUpdate();

        updateTaskId = Task.timer(0L, periodTicks, () -> {
            if (!closed) {
                updateLogic.run();
                refresh();
            }
        }).getTaskId();
    }

    public void stopAutoUpdate() {
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
    }

    public void onUpdate() {}

    public void setOpenSound(Sound sound, float volume, float pitch) {
        this.openSound = sound;
        this.openVolume = volume;
        this.openPitch = pitch;
    }

    public void playOpenSound(Player player) {
        if (openSound != null && player != null) {
            player.playSound(player.getLocation(), openSound, openVolume, openPitch);
        }
    }

    public void playClickSound(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }

    public void playSound(Player player, Sound sound, float volume, float pitch) {
        if (player != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void onClick(InventoryClickEvent event) {}

    public void onClose(InventoryCloseEvent event) {
        closed = true;
        stopAllAnimations();
        stopAutoUpdate();
    }

    public void setViewer(Player player) {
        this.viewer = player;
        playOpenSound(player);
    }

    public Player getViewer() {
        return viewer;
    }

    public boolean isClosed() {
        return closed;
    }

    public void handleItemClick(ClickEvent event) {
        if (clickActions.containsKey(event.slot())) {
            playClickSound(event.player());
            clickActions.get(event.slot()).accept(event);
        }
    }
}