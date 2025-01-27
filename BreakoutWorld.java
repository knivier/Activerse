import ActiverseEngine.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class BreakoutWorld extends World {;
    private static final int WORLD_WIDTH = 800;
    private static final int WORLD_HEIGHT = 600;
    ActiverseSound lobby = new ActiverseSound("dream-speedrun-music.wav");
    
    public BreakoutWorld() {
        super(WORLD_WIDTH, WORLD_HEIGHT, 1); // Creates a world of size 800x600 with 1x cell size
        setBackgroundImage("background.png"); // Sets the background image 
          // Creates a new ball object at position 300,500
        addObject(new Paddle(), 300,500); // Adds the paddle to bat the ball
        addObject(new Ball(400,450), 400, 450); // Adds the ball to the world
        lobby.play();
        for(int x = 0; x < 800; x += 100) {
            for(int y = 0; y < 300; y += 80) {
                addObject(new Brick(), x, y);
            }
        }
        showText(150, 150, "World started, 5 seconds");
        super.pause();
        Timer timer = new Timer(1000 / 30, new ActionListener() {
            int countdown = 90; // 3 sdeconds * 30 FPS

            @Override
            public void actionPerformed(ActionEvent e) {
            if (countdown > 0) {
                showText(150, 150, "World starts in: " + (countdown / 30) + " seconds");
                countdown--;
            } else {
                ((Timer) e.getSource()).stop();
                showText(150, 150, "");
                BreakoutWorld.this.resume();
            }
            }
        });
        timer.start(); 
    }

    @Override
    public void update(){
        super.update();
        Ball ball = (Ball) getActors().stream().filter(actor -> actor instanceof Ball).findFirst().orElse(null);
        if (ball != null) {
            showText(20, 20, "Balls Used: " + ball.getBallsUsed());
        }

        if(getActors().stream().noneMatch(actor -> actor instanceof Brick)){ // Checking for any instances of brick; let's not hard code this 
            showText(150, 150, "You  win!"); 
            javax.swing.JOptionPane.showMessageDialog(null, "You won!", "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public int getWorldWidth() {
        return WORLD_WIDTH;
    }

    public int getWorldHeight() {
        return WORLD_HEIGHT;
    }
}
