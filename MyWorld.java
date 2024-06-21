import ActiverseEngine.*;
/**
 * Represents a custom world for the application.
 * This class extends the World class to customize the world's initialization.
 *
 * @author Knivier
 */
public class MyWorld extends World {

    /**
     * Constructs a new instance of MyWorld with specific dimensions and cell size.
     * Initializes the world, sets the background image, and adds a MyActor object to it.
     */
    public MyWorld() {
        // Call the constructor of the superclass (World) with specified dimensions and cell size
        super(400, 400, 1);

        // Set the background image for the world
        setBackgroundImage("world.png");

        // Add a MyActor object to the world at the specified location (100, 100)
        addObject(new MyActor(), 100, 100);
        //end
    }
}
