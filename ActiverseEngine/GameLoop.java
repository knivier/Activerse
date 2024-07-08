package ActiverseEngine;

import java.util.Properties;
import java.io.IOException;

/**
 * Represents the main game loop for updating and rendering the game.
 */
public class GameLoop implements Runnable {
    private final World world;
    private final long FRAME_TIME;
    private int TARGET_FPS;

    public GameLoop(World world) {
        this.world = world;
        loadProperties();
        FRAME_TIME = 1000000000 / TARGET_FPS;
    }

    private void loadProperties() {
        Properties props = new Properties();
        String propertiesFile = "Activerse.properties";
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
            TARGET_FPS = Integer.parseInt(props.getProperty("fps", "60"));
        } catch (IOException e) {
            e.printStackTrace();
            TARGET_FPS = 60;
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        double delta = 0;
        int frames = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (double) FRAME_TIME;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        world.update();
    }

    private void render() {
        world.repaint();
    }
}