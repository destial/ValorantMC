package xyz.destiall.mc.valorant.utils;

import org.bukkit.scheduler.BukkitTask;

public class ScheduledTask {
    private final BukkitTask task;
    private final Runnable runnable;
    private boolean runOnCancel;
    public ScheduledTask(BukkitTask task, Runnable runnable) {
        this.runnable = runnable;
        this.task = task;
        runOnCancel = false;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void cancel() {
        if (task.isCancelled()) return;
        task.cancel();
        if (runOnCancel) runnable.run();
    }

    public void run() {
        runnable.run();
    }

    public void setRunOnCancel(boolean runOnCancel) {
        this.runOnCancel = runOnCancel;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public boolean willRunOnCancel() {
        return runOnCancel;
    }
}
