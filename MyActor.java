import ActiverseEngine.*;


/**
 * The base actor that extends the main Actor and can do more things.
 *
 * @author Knivier
 */
public class MyActor extends Actor {
    public MyActor() {
        // Creates new MyActor Object
        setImage(new ActiverseImage("player.png")); // Set image for the actor
    }

    @Override
    public void act() {
        // Move the player based on key presses
        if (keyIsDown('W')) {
            if (keyIsDown('M')) {
                move(4);
            }
            move(2); // Move forward

        }
        if (keyIsDown('S')) {
            move(-2); // Move backward
        }
        if (keyIsDown('A')) {
            turn(-Math.PI / 64); // Turn left (22.5 degrees)
        }
        if (keyIsDown('D')) {
            turn(Math.PI / 64); // Turn right (22.5 degrees)
        }
    }
}
