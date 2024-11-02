package ActiverseEngine;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * Represents a sound player for playing audio files.
 * This class provides methods to load, play, and stop audio files.
 * 
 * @author Knivier
 * @version 1.2.2
 */
public class ActiverseSound {
    private final String filename;
    private Clip clip;

    /**
     * Constructs a new ActiverseSound object with the specified audio file.
     *
     * @param filename The path to the audio file.
     */
    public ActiverseSound(String filename) {
        this.filename = filename;
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("4A.IN:(LN: ActiverseSound(String filename) - ACEHS Error thrown; an error occurred while loading the audio file (type: INTO). Please check the file path and try again. WAV files are only supported at this time.");
            e.printStackTrace();
        }
    }

    /**
     * Plays the loaded audio file.
     */
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
        else if(clip == null){
            throw new NullPointerException("4A.OUT:(LN: play() - ACEHS Error thrown; audio file is null. Please check the file path and try again.");
        }
    }

    /**
     * Stops the currently playing audio file.
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    /**
     * Checks if the audio file is currently playing.
     *
     * @return true if the audio file is playing, false otherwise.
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Gets the filename of the audio file.
     *
     * @return The filename of the audio file.
     */
    public String getFilename() {
        return filename;
    }
}
