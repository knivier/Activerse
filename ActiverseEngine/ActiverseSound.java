package ActiverseEngine;

import ActiverseUtils.ErrorLogger;
import ActiverseUtils.ResourcePaths;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;

/**
 * Represents a sound player for playing audio files.
 * This class provides methods to load, play, stop, and manage the volume of audio files.
 *
 * @version 1.4.1
 */
public class ActiverseSound {
    private final String filename;
    private Clip clip;
    private FloatControl volumeControl;

    /**
     * Constructs a new ActiverseSound object with the specified audio file.
     *
     * @param filename The path to the audio file.
     */
    public ActiverseSound(String filename) {
        this.filename = filename;
        try {
            AudioInputStream audioInputStream = openAudioInputStream(filename);
            try {
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } finally {
                audioInputStream.close();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            ErrorLogger.report("4A", "IN", "ActiverseSound(String filename)",
                    "an error occurred while loading the audio file (type: INTO). Please check the file path and try again. WAV files are only supported at this time.");
        }
    }

    private static AudioInputStream openAudioInputStream(String filename)
            throws IOException, UnsupportedAudioFileException {
        File file = new File(filename);
        if (file.isFile()) {
            return AudioSystem.getAudioInputStream(file);
        }
        InputStream in = ResourcePaths.openInputStream(filename);
        try {
            return AudioSystem.getAudioInputStream(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }

    /**
     * Plays the loaded audio file.
     */
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        } else {
            throw new NullPointerException(
                    ErrorLogger.format("4A", "OUT", "play()", "audio file is null. Please check the file path and try again."));
        }
    }

    /**
     * Stops the currently playing audio file.
     */
    public void stop() {
        if (clip != null) {
            clip.stop();
        } else {
            throw new NullPointerException(
                    ErrorLogger.format("4A", "OUT", "stop()", "audio file is null. Please check the file path and try again."));
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

    /**
     * Gets the current volume of the audio file.
     *
     * @return The current volume level (range: 0.0 to 1.0).
     */
    public float getVolume() {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float volume = (volumeControl.getValue() - min) / (max - min);
            return volume;
        }
        return 0.0f;
    }

    /**
     * Sets the volume of the audio file.
     *
     * @param volume The volume level to set (range: 0.0 to 1.0).
     */
    public void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float newVolume = min + (max - min) * volume;
            volumeControl.setValue(newVolume);
        }
    }

        /**
     * Loops the audio continuously.
     */
    public void loop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            throw new NullPointerException(
                    ErrorLogger.format("4A", "OUT", "loop()", "audio file is null. Cannot loop audio."));
        }
    }

    /**
     * Loops the audio a specified number of times.
     *
     * @param count The number of times to loop. Use Clip.LOOP_CONTINUOUSLY for infinite.
     */
    public void loop(int count) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(count);
        } else {
            throw new NullPointerException(
                    ErrorLogger.format("4A", "OUT", "loop(int count)", "audio file is null. Cannot loop audio."));
        }
    }

    /**
     * Pauses the currently playing audio.
     */
    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // stops but does not reset position
        }
    }

    /**
     * Resumes the audio from where it was paused.
     */
    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    /**
     * Seeks to a specific time position in the audio.
     *
     * @param milliseconds The position in milliseconds to seek to.
     */
    public void seek(long milliseconds) {
        if (clip != null) {
            int frame = (int) (milliseconds * clip.getFormat().getFrameRate() / 1000);
            clip.setFramePosition(frame);
        }
    }

    /**
     * Gets the current playback position in milliseconds.
     *
     * @return The current position in milliseconds.
     */
    public long getPosition() {
        if (clip != null) {
            return (long) (clip.getMicrosecondPosition() / 1000);
        }
        return 0;
    }

    /**
     * Gets the total length of the audio clip in milliseconds.
     *
     * @return The duration in milliseconds.
     */
    public long getLength() {
        if (clip != null) {
            return (long) (clip.getMicrosecondLength() / 1000);
        }
        return 0;
    }

    /**
     * Releases system resources associated with the audio clip.
     */
    public void dispose() {
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.close();
            } catch (Exception e) {
                ErrorLogger.reportException("4A", "IO", "dispose()", e);
            } finally {
                clip = null;
                volumeControl = null;
            }
        }
    }
    
    /**
     * Ensures resources are cleaned up when object is garbage collected.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            dispose();
        } finally {
            super.finalize();
        }
    }

}
