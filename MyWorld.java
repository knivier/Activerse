import ActiverseEngine.*;

/**
 * Represents the default world included with the package
 * It's recommended to read the wiki when modifying
 *
 * @author Knivier
 */
public class MyWorld extends World {

    /**
     * Constructs a new instance of MyWorld with specific dimensions and cell size.
     * Initializes the world, sets the background image, and adds a MyActor object to it.
     */
    public MyWorld() {
        super(600, 600, 1);
        addObject(new Player(), 100, 100);
    }

    @Override
    public void update() { // It's not recommended to modify this method unless you are advanced
        super.update();
    }
}
