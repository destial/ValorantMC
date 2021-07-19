package xyz.destiall.mc.valorant.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.destiall.mc.valorant.Valorant;

import java.util.ArrayList;

public class Scheduler {
    private static final ArrayList<ScheduledTask> TASKS = new ArrayList<>();

    public static ScheduledTask delay(Runnable runnable, long delay) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), runnable, delay);
        ScheduledTask scheduledTask = new ScheduledTask(task, runnable);
        TASKS.add(scheduledTask);
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> TASKS.remove(scheduledTask), delay);
        return scheduledTask;
    }

    public static ScheduledTask repeat(Runnable runnable, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Valorant.getInstance().getPlugin(), runnable, 0L, period);
        ScheduledTask scheduledTask = new ScheduledTask(task, runnable);
        scheduledTask.setRunOnCancel(true);
        TASKS.add(scheduledTask);
        return scheduledTask;
    }

    public static ScheduledTask delayAsync(Runnable runnable, long delay) {
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(Valorant.getInstance().getPlugin(), runnable, delay);
        ScheduledTask scheduledTask = new ScheduledTask(task, runnable);
        TASKS.add(scheduledTask);
        Bukkit.getScheduler().runTaskLater(Valorant.getInstance().getPlugin(), () -> TASKS.remove(scheduledTask), delay);
        return scheduledTask;
    }

    public static ScheduledTask repeatAsync(Runnable runnable, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Valorant.getInstance().getPlugin(), runnable, 0L, period);;
        ScheduledTask scheduledTask = new ScheduledTask(task, runnable);
        TASKS.add(scheduledTask);
        scheduledTask.setRunOnCancel(true);
        return scheduledTask;
    }

    public static void cancel(BukkitTask task) {
        ScheduledTask scheduledTask = TASKS.stream().filter(st -> st.getTask().equals(task)).findFirst().orElse(null);
        if (scheduledTask == null) return;
        TASKS.remove(scheduledTask);
        scheduledTask.cancel();
    }

    public static void forceRun(ScheduledTask task) {
        task.run();
    }

    public static void forceRun(BukkitTask task) {
        if (!task.isCancelled()) task.cancel();
        ScheduledTask scheduledTask = TASKS.stream().filter(st -> st.getTask().equals(task)).findFirst().orElse(null);
        if (scheduledTask == null) return;
        scheduledTask.run();
    }

    public static void cancel(ScheduledTask task) {
        TASKS.remove(task);
        task.cancel();
    }

    public static void cancelAll() {
        for (ScheduledTask task : TASKS) {
            task.cancel();
        }
        TASKS.clear();
    }
}
