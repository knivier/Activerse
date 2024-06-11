import javax.swing.JFrame;

public class Activerse {
    public static void start(World world) {
        JFrame frame = new JFrame("Activerse Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(world);
        frame.pack();
        frame.setVisible(true);
        world.start();
    }

    public static void stop(World world) {
        world.stop();
    }
}
