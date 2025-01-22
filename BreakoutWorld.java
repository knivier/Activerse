import ActiverseEngine.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class BreakoutWorld extends World {
    

    public BreakoutWorld() {
        super(800, 600, 1); // Creates a world of size 800x600 with 1x cell size
        setBackgroundImage("background.png"); // Sets the background image 
          // Creates a new ball object at position 300,500
        addObject(new Paddle(), 300,500); // Adds the paddle to bat the ball
        addObject(new Ball(400,450), 400, 450); // Adds the ball to the world
        addObject(new Brick(), 20 ,50 ); // Adding 3 bricks to the world
        addObject(new Brick(), 200 ,50 );
        addObject(new Brick(), 500 ,50 );
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

    public void update(){
        super.update();
        if(getActors().stream().noneMatch(actor -> actor instanceof Brick)){ // Checking for any instances of brick; let's not hard code this 
            showText(150, 150, "You  win!"); 
            javax.swing.JOptionPane.showMessageDialog(null, "You won!", "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}
