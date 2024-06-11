/**
 * The base actor that extends the main Actor and can do more things
 * 
 * @author Knivier
 */
public class MyActor extends Actor {
    public MyActor() { //creates new MyActor Object
        setImage(new ActiverseImage("actor.png"));
    }

    @Override
    public void act() {
        // Define behavior here
        move(2);
    }
}
