import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class World extends JPanel implements ActionListener {
    private Timer timer;
    private ArrayList<Actor> actors;

    public World(int width, int height, int cellSize) {
        setPreferredSize(new Dimension(width * cellSize, height * cellSize));
        setBackground(Color.WHITE);
        actors = new ArrayList<>();
        timer = new Timer(50, this);
    }

    public void addObject(Actor actor, int x, int y) {
        actor.setLocation(x, y);
        actor.setWorld(this);
        actors.add(actor);
    }

    public void removeObject(Actor actor) {
        actors.remove(actor);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Actor actor : actors) {
            actor.paint(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Actor actor : actors) {
            actor.act();
        }
        repaint();
    }
}
