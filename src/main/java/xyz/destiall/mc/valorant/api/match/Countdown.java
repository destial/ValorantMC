package xyz.destiall.mc.valorant.api.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import xyz.destiall.mc.valorant.utils.Formatter;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;

public class Countdown implements Module {
    private final BossBar bossBar;
    private final Context context;
    private Runnable func;
    private Duration timer;
    private ScheduledTask repeatTask;
    private long startTime;
    public Countdown(Context context) {
        this.context = context;
        this.timer = Duration.ofSeconds(context.getTime());
        func = null;
        bossBar = Bukkit.createBossBar(context.getTitle(), context.getColor(), BarStyle.SOLID);
        bossBar.setVisible(true);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        repeatTask = Scheduler.repeat(() -> {
            timer = timer.minusMillis(System.currentTimeMillis() - startTime);
            if (timer.isZero() || timer.isNegative()) {
                if (func != null) func.run();
                stop();
                return;
            }
            startTime = System.currentTimeMillis();
            double progress = bossBar.getProgress();
            ChatColor c = ChatColor.WHITE;
            if (progress < 0.25 && bossBar.getColor() != BarColor.RED) {
                bossBar.setColor(BarColor.RED);
            }
            if (progress < 0.25) {
                c = ChatColor.RED;
            }
            bossBar.setTitle(context.getTitle() + c + Formatter.duration(timer));
            bossBar.setProgress((float) timer.getSeconds() / context.getTime());
        }, 1L);
    }

    public void stop() {
        repeatTask.cancel();
        bossBar.removeAll();
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public Context getContext() {
        return context;
    }

    public void onComplete(Runnable runnable) {
        this.func = runnable;
    }

    public enum Context {
        ROUND_ENDING(5, "Next Round in ", BarColor.BLUE),
        ROUND_STARTING(30, "Round Starting in ", BarColor.BLUE),
        BEFORE_SPIKE(100, "", BarColor.WHITE),
        AFTER_SPIKE(45, "", BarColor.WHITE);

        private final long time;
        private final String title;
        private final BarColor color;
        Context(long time, String title, BarColor color) {
            this.time = time;
            this.title = title;
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public long getTime() {
            return time;
        }

        public BarColor getColor() {
            return color;
        }
    }
}
