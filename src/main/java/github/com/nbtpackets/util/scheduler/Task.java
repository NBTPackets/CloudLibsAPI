package github.com.nbtpackets.util.scheduler;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import github.com.nbtpackets.CloudLibAPI;

import java.util.function.Consumer;

@UtilityClass
public class Task {

    public BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(CloudLibAPI.getInstance(), runnable);
    }

    public BukkitTask later(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLater(CloudLibAPI.getInstance(), runnable, delay);
    }

    public BukkitTask timer(long delay, long period, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimer(CloudLibAPI.getInstance(), runnable, delay, period);
    }

    public BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(CloudLibAPI.getInstance(), runnable);
    }

    public BukkitTask laterAsync(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(CloudLibAPI.getInstance(), runnable, delay);
    }

    public BukkitTask timerAsync(long delay, long period, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(CloudLibAPI.getInstance(), runnable, delay, period);
    }

    public void timerSelf(long delay, long period, Consumer<BukkitTask> consumer) {
        Bukkit.getScheduler().runTaskTimer(CloudLibAPI.getInstance(), task -> consumer.accept(task), delay, period);
    }

    public void timerAsyncSelf(long delay, long period, Consumer<BukkitTask> consumer) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(CloudLibAPI.getInstance(), task -> consumer.accept(task), delay, period);
    }
}