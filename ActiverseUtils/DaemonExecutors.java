package ActiverseUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shared factory for daemon {@link ExecutorService}s so game code does not repeat thread boilerplate.
 */
public final class DaemonExecutors {

    private DaemonExecutors() {}

    public static ExecutorService newSingleDaemonThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(r -> daemonThread(threadName, r));
    }

    public static ExecutorService newFixedDaemonThreadPool(int nThreads, String threadName) {
        return Executors.newFixedThreadPool(nThreads, r -> daemonThread(threadName, r));
    }

    private static Thread daemonThread(String name, Runnable r) {
        Thread t = new Thread(r, name);
        t.setDaemon(true);
        return t;
    }
}
