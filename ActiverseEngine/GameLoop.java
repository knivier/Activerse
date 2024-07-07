package ActiverseEngine;

/**
 * Represents the main game loop for updating and rendering the game.
 */
public class GameLoop implements Runnable {
    private final World world;
    private final int TARGET_FPS = 60;
    private final long FRAME_TIME = 1000000000 / TARGET_FPS;

    public GameLoop(World world) {
        this.world = world;
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

            // FPS counter (optional)
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }

            try {
                Thread.sleep(1); // Adjust sleep time if needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        world.update(); // Update game state
    }

    private void render() {
        world.repaint(); // Render game
    }
}
