package ActiverseUtils;

/**
 * Advanced Timer class for game loop timing, delays, and repeated events.
 * Part of the Activerse engine.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class Timer {
    private long startTime;       // when the timer started
    private long pauseTime;       // when the timer was paused
    private boolean running;
    private boolean paused;
    private long duration;        // in milliseconds
    private boolean repeat;

    /**
     * Constructs a Timer with optional duration and repeat flag.
     * @param durationMillis Timer duration in milliseconds (0 for infinite)
     * @param repeat Whether the timer repeats after duration
     */
    public Timer(long durationMillis, boolean repeat) {
        this.duration = durationMillis;
        this.repeat = repeat;
        this.running = false;
        this.paused = false;
    }

    /**
     * Constructs a Timer with no duration (manual control).
     */
    public Timer() {
        this(0, false);
    }

    /** Starts or restarts the timer. */
    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
        paused = false;
    }

    /** Stops the timer completely. */
    public void stop() {
        running = false;
        paused = false;
        startTime = 0;
        pauseTime = 0;
    }

    /** Pauses the timer. */
    public void pause() {
        if (running && !paused) {
            pauseTime = System.currentTimeMillis();
            paused = true;
        }
    }

    public void resume() {
        if (running && paused) {
            long pausedDuration = System.currentTimeMillis() - pauseTime;
            startTime += pausedDuration;
            paused = false;
        }
    }

    /** Resets the timer without stopping it. */
    public void reset() {
        startTime = System.currentTimeMillis();
        paused = false;
    }

    /**
     * Returns how much time has elapsed in milliseconds.
     */
    public long getElapsedTime() {
        if (!running) return 0;
        if (paused) return pauseTime - startTime;
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the remaining time in milliseconds, or 0 if expired.
     */
    public long getRemainingTime() {
        if (duration == 0) return Long.MAX_VALUE; // infinite
        return Math.max(0, duration - getElapsedTime());
    }

    /**
     * Returns true if the timer has finished (only applies if duration > 0).
     * Automatically resets if repeating.
     */
    public boolean isFinished() {
        if (!running || duration == 0) return false;
        if (getElapsedTime() >= duration) {
            if (repeat) reset();
            else running = false;
            return true;
        }
        return false;
    }

    /** Returns true if the timer is currently running (not paused or stopped). */
    public boolean isRunning() {
        return running && !paused;
    }

    /** Returns true if the timer is paused. */
    public boolean isPaused() {
        return paused;
    }

    /** Returns true if the timer is set to repeat. */
    public boolean isRepeating() {
        return repeat;
    }

    /** Set a new duration for the timer (in milliseconds). */
    public void setDuration(long durationMillis) {
        this.duration = durationMillis;
    }

    /** Set whether the timer should repeat. */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
