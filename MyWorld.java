/**
 * Represents a custom world for the application.
 * This class extends the World class to customize the world's initialization.
 *
 * @author Knivier
 */
public class MyWorld extends World {
    
    /**
     * Constructs a new instance of MyWorld with specific dimensions and cell size.
     * Initializes the world and adds a MyActor object to it.
     */
    public MyWorld() {
        // Call the constructor of the superclass (World) with specified dimensions and cell size
        super(600, 400, 1);
        
        // Add a MyActor object to the world at the specified location (100, 100)
        addObject(new MyActor(), 100, 100);
    }
}
