package ActiverseUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Single shared daemon scheduler so {@link ActiverseEngine.Actor#delayNext} does not spawn unbounded threads.
 */
public final class DelayScheduler {

    private static volatile ScheduledExecutorService sched = createExecutor();

    private DelayScheduler() {}

    private static ScheduledExecutorService createExecutor() {
        return Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ActiverseDelay");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Runs {@code task} after {@code delayMs} on a background thread (or immediately if {@code delayMs <= 0}).
     */
    public static void runAfterMillis(long delayMs, Runnable task) {
        if (task == null) {
            return;
        }
        if (delayMs <= 0) {
            task.run();
            return;
        }
        synchronized (DelayScheduler.class) {
            if (sched == null || sched.isShutdown()) {
                sched = createExecutor();
            }
            sched.schedule(task, delayMs, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops delayed tasks (e.g. on JVM exit). Safe to call more than once.
     */
    public static void shutdown() {
        ScheduledExecutorService local;
        synchronized (DelayScheduler.class) {
            local = sched;
        }
        if (local == null) return;
        local.shutdown();
        try {
            if (!local.awaitTermination(2, TimeUnit.SECONDS)) {
                local.shutdownNow();
                local.awaitTermination(2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            local.shutdownNow();
        }
    }
}
