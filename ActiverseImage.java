import java.awt.Image;
import java.awt.Toolkit;

public class ActiverseImage {
    private Image image;

    public ActiverseImage(String filename) {
        image = Toolkit.getDefaultToolkit().getImage(filename);
    }

    public Image getImage() {
        return image;
    }
}
