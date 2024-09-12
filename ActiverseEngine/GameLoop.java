package ActiverseEngine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents the main game loop for updating and rendering the game.
 */
public class GameLoop implements Runnable {
    private final World world;
    private final long FRAME_TIME;
    private int TARGET_FPS;
    private int frames;
    private long lastFpsTime;
    private boolean dynamicLighting;
    private volatile boolean running = true;

    public GameLoop(World world) {
        this.world = world;
        loadProperties();
        FRAME_TIME = 1000000000 / TARGET_FPS;
        frames = 0;
        lastFpsTime = System.nanoTime();
    }

    private void loadProperties() {
        Properties props = new Properties();
        String propertiesFile = "Activerse.properties";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + propertiesFile);
                TARGET_FPS = 60;
                dynamicLighting = false;
                return;
            }
            props.load(input);
            TARGET_FPS = Integer.parseInt(props.getProperty("fps", "60"));
            dynamicLighting = Boolean.parseBoolean(props.getProperty("dynamicLighting", "false"));
        } catch (IOException e) {
            e.printStackTrace();
            TARGET_FPS = 60;
            dynamicLighting = false;
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) FRAME_TIME;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            render();

            frames++;
            calculateFPS(now);

            long frameTime = System.nanoTime() - lastTime;
            long sleepTime = FRAME_TIME - frameTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Thread.yield();
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void update() {
        world.update();
    }

    private void render() {
        world.repaint();
    }

    private void calculateFPS(long now) {
        if (now - lastFpsTime >= 1_000_000_000) {
            World.setFPS(frames);
            frames = 0;
            lastFpsTime += 1_000_000_000;
        }
    }
}