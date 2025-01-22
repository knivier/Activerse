import ActiverseEngine.*;

/**
 * @author Knivier
 * @see Actor
 */
public class Paddle extends Actor{
    // Constructor for Paddle object with image type paddle.png and height of 10
    public Paddle(){
        setImage(new ActiverseImage("paddle.png"));
        setHeight(10);
    }

    // Act method for the Paddle, which uses methods detectMovement and detectCollisions
    @Override
    public void act(){
        detectMovement();
        detectCollisions();
    }

    // Movement system
    public void detectMovement(){
        if (keyIsDown('a')){
            setX(getX() - 5);
        }
        if (keyIsDown('d')){
            setX(getX() + 5); // Velocity systems
        }
        stayInsideBoundary(); // Makes sure paddle stays inside the boundary
    }

    // Collision detection system between ball and paddle
    public void detectCollisions(){
        getWorld().getActors().forEach(actor -> { // Lambda expression to iterate through all actors in the world
            if (actor instanceof Ball ball) {
                if(this.intersects(ball)){
                    ball.handlePaddleCollision(this);
                }
            }
        });
    }

    public void stayInsideBoundary(){ // Method to keep paddle inside the boundary
        if (getX() < getWidth() / 2) {
            setX(getWidth() / 2);
        }
        if (getX() > getWorld().getWidth() - getWidth()) {
            setX(getWorld().getWidth() - getWidth());
        }
    }

}

