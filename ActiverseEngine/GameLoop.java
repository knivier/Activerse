package ActiverseEngine;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * Represents a highly parallelized game loop maximizing CPU usage while maintaining stability.
 * Threads are split across update/render responsibilities with precise frame pacing.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class GameLoop implements Runnable {
    private final World world;
    private final long FRAME_TIME_NANOS;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private long TARGET_FPS;
    private volatile int frames;
    private volatile int updates;
    private long lastFpsTime;
    private boolean dynamicLighting;
    private Thread updateThread;
    private Thread renderThread;

    // Optional thread for input/event processing in the future
    // private Thread inputThread;

    public GameLoop(World world) {
        this.world = world;
        TARGET_FPS = ConfigPuller.getInt("fps", 60);
        dynamicLighting = ConfigPuller.getBoolean("dynamicLighting", false);
        FRAME_TIME_NANOS = 1_000_000_000L / TARGET_FPS;
        lastFpsTime = System.nanoTime();
    }

    /**
     * Starts the game loop on separate threads for update and render.
     * This method initializes the threads and begins the game loop.
     */
    public void start() {
        if (running.get()) return;

        running.set(true);
        updateThread = new Thread(this::updateLoop, "UpdateThread");
        renderThread = new Thread(this::renderLoop, "RenderThread");

        updateThread.start();
        renderThread.start();
    }

    /**
     * Stops the game loop and waits for threads to finish.
     * This method safely terminates the update and render threads,
     */
    public void stop() {
        running.set(false);
        Thread current = Thread.currentThread();
        if (updateThread != null && updateThread != current) {
            updateThread.interrupt();
        }
        if (renderThread != null && renderThread != current) {
            renderThread.interrupt();
        }
        try {
            // Never join the current thread (e.g. stop() from world.update() on UpdateThread deadlocks).
            if (updateThread != null && updateThread.isAlive() && updateThread != current) {
                updateThread.join(2000);
                if (updateThread.isAlive()) {
                    System.err.println("[Activerse] Warning: Update thread did not terminate in time.");
                }
            }
            if (renderThread != null && renderThread.isAlive() && renderThread != current) {
                renderThread.join(2000);
                if (renderThread.isAlive()) {
                    System.err.println("[Activerse] Warning: Render thread did not terminate in time.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Activerse] Error stopping game loop threads.");
            e.printStackTrace();
        }
        updateThread = null;
        renderThread = null;
        System.out.println("[Activerse] Game loop stopped.");
    }

    /**
     * Update loop synchronized to system monotonic clock. Each tick is scheduled relative
     * to a fixed epoch so timing drift cannot accumulate. If the loop falls behind, missed
     * ticks are skipped rather than burst-processed, preventing spiral-of-death hitching.
     */
    private void updateLoop() {
        final long epoch = System.nanoTime();
        long lastTickNumber = -1;

        while (running.get()) {
            long now = System.nanoTime();
            long currentTickNumber = (now - epoch) / FRAME_TIME_NANOS;

            if (currentTickNumber <= lastTickNumber) {
                long nextTickAt = epoch + (lastTickNumber + 1) * FRAME_TIME_NANOS;
                long sleepNanos = nextTickAt - System.nanoTime();
                if (sleepNanos > 0) {
                    if (sleepNanos > 2_000_000L) {
                        try { Thread.sleep(sleepNanos / 1_000_000L, (int) (sleepNanos % 1_000_000L)); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                    } else {
                        LockSupport.parkNanos(sleepNanos);
                    }
                }
                continue;
            }

            synchronized (world) {
                if (!world.isSimulationDrivenBySwingTimer()) {
                    world.update();
                }
            }
            updates++;
            lastTickNumber = currentTickNumber;
        }
    }

    /**
     * Render loop synchronized to system monotonic clock, matching the update loop's
     * epoch-based scheduling for smooth, consistent frame pacing.
     */
    private void renderLoop() {
        final long epoch = System.nanoTime();
        long lastFrameNumber = -1;

        while (running.get()) {
            long now = System.nanoTime();
            long currentFrameNumber = (now - epoch) / FRAME_TIME_NANOS;

            if (currentFrameNumber <= lastFrameNumber) {
                long nextFrameAt = epoch + (lastFrameNumber + 1) * FRAME_TIME_NANOS;
                long sleepNanos = nextFrameAt - System.nanoTime();
                if (sleepNanos > 0) {
                    if (sleepNanos > 2_000_000L) {
                        try { Thread.sleep(sleepNanos / 1_000_000L, (int) (sleepNanos % 1_000_000L)); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                    } else {
                        LockSupport.parkNanos(sleepNanos);
                    }
                }
                continue;
            }

            world.repaint();
            frames++;
            calculateFPS(now);
            lastFrameNumber = currentFrameNumber;
        }
    }

    /**
     * Calculates and updates the frames per second (FPS) based on the elapsed time.
     * This method is called periodically to update the FPS value in the World class.
     *
     * @param now the current time in nanoseconds
     */
    private void calculateFPS(long now) {
        if (now - lastFpsTime >= 1_000_000_000L) {
            World.setFPS(frames);
            frames = 0;
            updates = 0;
            lastFpsTime = now;
        }
    }

    @Override
    public void run() {
        start();
    }

    /**
     * Returns the target frames per second (FPS) for the game loop.
     * This value is loaded from the properties file or defaults to 60 FPS.
     *
     * @return the target FPS
     */
    public long getTargetFps() {
        return TARGET_FPS;
    }

    /**
     * Returns whether dynamic lighting is enabled in the game loop.
     * This value is loaded from the properties file or defaults to false.
     *
     * @return true if dynamic lighting is enabled, false otherwise
     */
    public boolean isDynamicLighting() {
        return dynamicLighting;
    }
}
