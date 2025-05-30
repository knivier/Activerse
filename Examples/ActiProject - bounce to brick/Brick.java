
import ActiverseEngine.*;

public class Brick extends Actor {
    public Brick() {
        setImage(new ActiverseImage("assets/images/brick.png"));
    }

    @Override 
    public void act(){        
        // Bricks don't do anything, so we can leave this blank
    }
    
}
