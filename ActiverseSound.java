import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ActiverseSound {
    private Clip clip;

    public ActiverseSound(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
