package github.com.nbtpackets.util.scheduler;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import github.com.nbtpackets.CloudLibsAPI;

import java.util.function.Consumer;

@UtilityClass
public class Task {

    public BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(CloudLibsAPI.getInstance(), runnable);
    }

    public BukkitTask later(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLater(CloudLibsAPI.getInstance(), runnable, delay);
    }

    public BukkitTask timer(long delay, long period, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimer(CloudLibsAPI.getInstance(), runnable, delay, period);
    }

    public BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(CloudLibsAPI.getInstance(), runnable);
    }

    public BukkitTask laterAsync(long delay, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(CloudLibsAPI.getInstance(), runnable, delay);
    }

    public BukkitTask timerAsync(long delay, long period, Runnable runnable) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(CloudLibsAPI.getInstance(), runnable, delay, period);
    }

    public void timerSelf(long delay, long period, Consumer<BukkitTask> consumer) {
        Bukkit.getScheduler().runTaskTimer(CloudLibsAPI.getInstance(), task -> consumer.accept(task), delay, period);
    }

    public void timerAsyncSelf(long delay, long period, Consumer<BukkitTask> consumer) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(CloudLibsAPI.getInstance(), task -> consumer.accept(task), delay, period);
    }
}