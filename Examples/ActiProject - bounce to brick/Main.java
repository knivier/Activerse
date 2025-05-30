import ActiverseEngine.Activerse;

/**
 * Instantiates a new myWorld class and starts the game engine
 *
 * @author Knivier
 */
public class Main {
    
    /** 
     * @param args console arguments, none are valid, all are not
     */
    public static void main(String[] args) {
        Activerse.start(new BreakoutWorld()); // Starts breakout
    }
}
