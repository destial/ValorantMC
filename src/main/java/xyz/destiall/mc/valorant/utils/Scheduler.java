package xyz.destiall.mc.valorant.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.destiall.mc.valorant.Valorant;

import java.util.ArrayList;

public class Scheduler {
    private static final ArrayList<BukkitTask> TASKS = new ArrayList<>();

    public static BukkitTask delay(Runnable runnable, long delay) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), runnable, delay);
        TASKS.add(task);
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> TASKS.remove(task), delay);
        return task;
    }

    public static BukkitTask repeat(Runnable runnable, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Valorant.getInstance().getPlugin(), runnable, 0L, period);;
        TASKS.add(task);
        return task;
    }

    public static BukkitTask delayAsync(Runnable runnable, long delay) {
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(Valorant.getInstance().getPlugin(), runnable, delay);
        TASKS.add(task);
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> TASKS.remove(task), delay);
        return task;
    }

    public static BukkitTask repeatAsync(Runnable runnable, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Valorant.getInstance().getPlugin(), runnable, 0L, period);;
        TASKS.add(task);
        return task;
    }

    public static void cancel(BukkitTask task) {
        if (!task.isCancelled()) task.cancel();
        TASKS.remove(task);
    }

    public static void cancelAll() {
        for (BukkitTask task : TASKS) {
            if (!task.isCancelled()) task.cancel();
        }
        TASKS.clear();
    }
}
