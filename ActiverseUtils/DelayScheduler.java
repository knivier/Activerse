package ActiverseUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Single shared daemon scheduler so {@link ActiverseEngine.Actor#delayNext} does not spawn unbounded threads.
 */
public final class DelayScheduler {

    private static final ScheduledExecutorService SCHED = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ActiverseDelay");
        t.setDaemon(true);
        return t;
    });

    private DelayScheduler() {}

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
        SCHED.schedule(task, delayMs, TimeUnit.MILLISECONDS);
    }
}
