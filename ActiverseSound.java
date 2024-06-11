import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Represents a sound player for playing audio files.
 * This class provides methods to load, play, and stop audio files.
 *
 * @author Knivier
 */
public class ActiverseSound {
    private Clip clip;

    /**
     * Constructs a new ActiverseSound object with the specified audio file.
     *
     * @param filename The path to the audio file.
     */
    public ActiverseSound(String filename) {
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays the loaded audio file.
     */
    public void play() {
        if (clip != null) {
            // Set the frame position to the beginning and start playing
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Stops the currently playing audio file.
     */
    public void stop() {
        if (clip != null) {
            // Stop playing
            clip.stop();
        }
    }
}
