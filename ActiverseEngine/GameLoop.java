package ActiverseEngine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a highly parallelized game loop maximizing CPU usage while maintaining stability.
 * Threads are split across update/render responsibilities with precise frame pacing.
 * @author Knivier
 * @version 1.5.0
 */
public class GameLoop implements Runnable {
    private final World world;
    private long TARGET_FPS;
    private final long FRAME_TIME_NANOS;

    private volatile int frames;
    private volatile int updates;
    private long lastFpsTime;

    private boolean dynamicLighting;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread updateThread;
    private Thread renderThread;

    // Optional thread for input/event processing in the future
    // private Thread inputThread;

    public GameLoop(World world) {
        this.world = world;
        loadProperties();
        FRAME_TIME_NANOS = 1_000_000_000L / TARGET_FPS;
        lastFpsTime = System.nanoTime();
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("Activerse.properties")) {
            if (input != null) {
                props.load(input);
                TARGET_FPS = Integer.parseInt(props.getProperty("fps", "60"));
                dynamicLighting = Boolean.parseBoolean(props.getProperty("dynamicLighting", "false"));
            } else {
                System.err.println("[Activerse] Properties file not found. Using default settings.");
                TARGET_FPS = 60;
                dynamicLighting = false;
            }
        } catch (IOException e) {
            System.err.println("[Activerse] Error loading properties. Using default settings.");
            TARGET_FPS = 60;
            dynamicLighting = false;
        }
    }

    public void start() {
        if (running.get()) return;

        running.set(true);
        updateThread = new Thread(this::updateLoop, "UpdateThread");
        renderThread = new Thread(this::renderLoop, "RenderThread");

        updateThread.start();
        renderThread.start();

        System.out.println("[Activerse] Game loop started on multiple threads.");
    }

    public void stop() {
        running.set(false);
        try {
            updateThread.join();
            renderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[Activerse] Game loop stopped.");
    }

    private void updateLoop() {
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running.get()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) FRAME_TIME_NANOS;
            lastTime = now;

            // Process all update ticks necessary
            while (delta >= 1) {
                synchronized (world) {
                    world.update(); // Update game state
                }
                updates++;
                delta--;
            }

            spinWaitNano(250_000); // short delay to reduce wasted CPU
        }
    }

    private void renderLoop() {
        while (running.get()) {
            long start = System.nanoTime();

            synchronized (world) {
                world.repaint(); // Safe rendering
            }

            frames++;
            calculateFPS(start);

            long frameTime = System.nanoTime() - start;
            long sleepTime = FRAME_TIME_NANOS - frameTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000L, (int) (sleepTime % 1_000_000L));
                } catch (InterruptedException ignored) {}
            } else {
                // Frame overran; yield CPU briefly to avoid complete spin
                Thread.yield();
            }
        }
    }

    private void calculateFPS(long now) {
        if (now - lastFpsTime >= 1_000_000_000L) {
            World.setFPS(frames);
            frames = 0;
            updates = 0;
            lastFpsTime = now;
        }
    }

    /**
     * Uses a short busy-wait to lightly throttle CPU in tight loops.
     * Useful for high-frequency loops without costly sleep overhead.
     */
    private void spinWaitNano(long duration) {
        long target = System.nanoTime() + duration;
        while (System.nanoTime() < target) {
            // Minimal wait – burn a bit of CPU but improves timing precision
        }
    }

    @Override
    public void run() {
        start();
    }

    public long getTargetFps() {
        return TARGET_FPS;
    }

    public boolean isDynamicLighting() {
        return dynamicLighting;
    }
}
