package xyz.destiall.mc.valorant.utils;

import org.bukkit.scheduler.BukkitTask;

public class ScheduledTask {
    private final BukkitTask task;
    private final Runnable runnable;
    private final TaskType type;
    private boolean runOnCancel;
    public ScheduledTask(BukkitTask task, Runnable runnable, TaskType type) {
        this.runnable = runnable;
        this.task = task;
        this.type = type;
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

    public TaskType getTaskType() {
        return type;
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

    enum TaskType {
        DELAY,
        REPEATED
    }
}
