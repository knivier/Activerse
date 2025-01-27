import ActiverseEngine.*;
import java.awt.*;

public class Ball extends Actor {
    private int x, y;
    private int ballsUsed = 0;
    private final int diameter = 20;
    private int xSpeed = 2, ySpeed = -2;
    private boolean initialMove = true;
    public ActiverseSound bounce = new ActiverseSound("bounce.wav");
    public ActiverseSound boom = new ActiverseSound("explosion.wav");

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        setLocation(x, y); // Set initial position
        setImage(new ActiverseImage("ball.png"));
        boom.setVolume(0.8f);
        bounce.setVolume(0.75f);
    }
    
    @Override
    public void act() {
        // Removed initial move logic
        updatePosition();
        getWorld().getActors().forEach(actor -> {
            switch (actor) {
                case Paddle paddle -> handlePaddleCollision(paddle);
                case Brick brick -> {
                    if (handleBrickCollision(brick)) {
                        getWorld().removeObject(actor);
                    }
                }
                default -> {
                    // Handle other types of actors if necessary
                }
            }
        });
    }

    public void updatePosition() {
        x += xSpeed;
        y += ySpeed;
        setLocation(x, y); // Update the ball's position
        // Bounce off walls
        if (x < 0 || x > getWorld().getWidth() - diameter) {
            xSpeed = -xSpeed;
            bounce.play();
        }
        if (y < 0) {
            ySpeed = -ySpeed;
            bounce.play();
        }
        // Reset velocity if the ball goes out of bounds
        if (y > getWorld().getHeight()) {
            xSpeed = 2;
            ySpeed = -2;
            x = 400;
            y = 450;
            setLocation(x, y);
            initialMove = true;
            ballsUsed++;
        }
    }

    public int getBallsUsed() {
        return ballsUsed;
    }

    public void handlePaddleCollision(Paddle paddle) {
        // Check collision with paddle
        if (y + diameter >= paddle.getY() && x + diameter > paddle.getX() && x < paddle.getX() + paddle.getWidth()) {
            int paddleSection = paddle.getWidth() / 4;
            int ballPosition = x - paddle.getX();

            if (ballPosition < paddleSection) {
                xSpeed = -Math.abs(xSpeed) - 1; // Far left section
            } else if (ballPosition < 2 * paddleSection) {
                xSpeed = -Math.abs(xSpeed); // Left middle section
            } else if (ballPosition < 3 * paddleSection) {
                xSpeed = Math.abs(xSpeed); // Right middle section
            } else {
                xSpeed = Math.abs(xSpeed) + 1; // Far right section
            }

            ySpeed = -ySpeed;
            y = paddle.getY() - diameter; // Reset position
            bounce.play();
        }
    }

    public boolean handleBrickCollision(Brick brick) {
        if (this.intersects(brick)) {
            // Determine the side of collision
            if (x + diameter - xSpeed <= brick.getX() || x - xSpeed >= brick.getX() + brick.getWidth()) {
                xSpeed = -xSpeed; // Horizontal collision
            } else {
                ySpeed = -ySpeed; // Vertical collision
            }
            bounce.play();
            boom.play();
            return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(getX(), getY(), diameter, diameter);
    }

    @Override
    protected Rectangle getBoundingBox() {
        return new Rectangle(getX(), getY(), diameter, diameter);
    }
}
