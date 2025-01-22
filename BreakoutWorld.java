import ActiverseEngine.*;

public class BreakoutWorld extends World {
    

    public BreakoutWorld() {
        super(800, 600, 1); // Creates a world of size 800x600 with 1x cell size
        setBackgroundImage("background.png"); // Sets the background image 
          // Creates a new ball object at position 300,500
        addObject(new Paddle(), 300,500); // Adds the paddle to bat the ball
        addObject(new Ball(300,500), 300, 500); // Adds the ball to the world
        addObject(new Brick(), 20 ,50 );
        addObject(new Brick(), 200 ,50 );
        addObject(new Brick(), 500 ,50 );
    }

    public void update(){
        super.update();
        if(getActors().stream().noneMatch(actor -> actor instanceof Brick)){
            showText(150, 150, "You  win!");
            javax.swing.JOptionPane.showMessageDialog(null, "You won!", "Game Over", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}
