package xyz.destiall.mc.valorant.api.match;

import org.bukkit.boss.BarColor;
import xyz.destiall.mc.valorant.utils.ScheduledTask;
import xyz.destiall.mc.valorant.utils.Scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Countdown implements Module {
    private final List<Consumer<Duration>> events;
    private final Context context;

    private Runnable func;
    private Duration timer;
    private ScheduledTask repeatTask;
    private long startTime;

    public Countdown(Context context) {
        this.context = context;
        this.timer = Duration.ofSeconds(context.getTime());
        events = new ArrayList<>();
        func = null;
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
            for (Consumer<Duration> event : events) {
                event.accept(timer);
            }
        }, 1L);
        repeatTask.setRunOnCancel(false);
    }

    public void stop() {
        repeatTask.cancel();
        events.clear();
        func = null;
    }

    public Duration getRemaining() {
        return timer;
    }

    public Context getContext() {
        return context;
    }

    public void onComplete(Runnable runnable) {
        this.func = runnable;
    }

    public void addEvent(Consumer<Duration> runnable) {
        events.add(runnable);
    }

    @Override
    public void destroy() {
        stop();
    }

    public enum Context {
        ROUND_ENDING(10, "Next Round in ", BarColor.BLUE),
        ROUND_STARTING(5, "Round Starting in ", BarColor.BLUE),
        BEFORE_SPIKE(100, "Pre Plant: ", BarColor.WHITE),
        AFTER_SPIKE(45, "Post Plant: ", BarColor.WHITE);

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
