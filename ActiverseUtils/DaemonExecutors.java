package ActiverseUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shared factory for daemon {@link ExecutorService}s so game code does not repeat thread boilerplate.
 */
public final class DaemonExecutors {

    private DaemonExecutors() {}

    /**
     * Creates a single-thread daemon executor.
     * <p>
     * Daemon threads do not prevent JVM shutdown, which is useful for background
     * save/flush tasks during game exit.
     *
     * @param threadName Thread name to assign
     * @return Single-thread daemon executor
     */
    public static ExecutorService newSingleDaemonThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(r -> daemonThread(threadName, r));
    }

    /**
     * Creates a fixed-size daemon thread pool.
     *
     * @param nThreads Number of worker threads
     * @param threadName Base thread name
     * @return Fixed daemon thread pool
     */
    public static ExecutorService newFixedDaemonThreadPool(int nThreads, String threadName) {
        return Executors.newFixedThreadPool(nThreads, r -> daemonThread(threadName, r));
    }

    private static Thread daemonThread(String name, Runnable r) {
        Thread t = new Thread(r, name);
        t.setDaemon(true);
        return t;
    }
}
