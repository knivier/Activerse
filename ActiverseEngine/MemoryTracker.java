package ActiverseEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ActiverseUtils.ErrorLogger;

/**
 * MemoryTracker class is used to track memory usage of the game along with other statistics
 *
 * @author Knivier
 * @version 1.3.2
 */
public class MemoryTracker {
    private long lastTime;
    private long lastMemoryUsed;
    private double memoryUsedPerSecond;
    private boolean loggingEnabled;
    private int targetFPS;
    private boolean newSessionNotified = false;
    private int logNum = 0;
    private PrintWriter logWriter;

    /**
     * Constructor for MemoryTracker class
     */
    public MemoryTracker() {
        loadProperties();
        lastTime = System.nanoTime();
        lastMemoryUsed = getMemoryUsed();
        if (loggingEnabled) {
            try {
                logWriter = new PrintWriter(new FileWriter("logs.log", true));
            } catch (IOException e) {
                ErrorLogger.reportException("9A", "IO.OUT", "MemoryTracker()", e);
                logWriter = null;
            }
        }
    }


    /**
     * Returns FPS target
     *
     * @return int FPStarget
     */
    public int getTargetFPS() {
        return targetFPS;
    }

    /**
     * Loads logging/FPS preferences via {@link ConfigPuller} ({@code Activerse.properties} engine keys).
     */
    private void loadProperties() {
        loggingEnabled = ConfigPuller.getBoolean("logging", false);
        targetFPS = ConfigPuller.getInt("fps", 60);
    }


    /**
     * Returns the memory used
     *
     * @return long
     */
    private long getMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }


    /**
     * Returns the memory heap usage
     *
     * @return MemoryUsage obj
     */
    private MemoryUsage getHeapMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage();
    }


    /**
     * Returns the non heap memory usage
     *
     * @return MemoryUsage
     */
    private MemoryUsage getNonHeapMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getNonHeapMemoryUsage();
    }

    /**
     * Returns the garbage collection time
     *
     * @return long garbage collection time
     */
    private long getGarbageCollectionTime() {
        long totalGarbageCollectionTime = 0;
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalGarbageCollectionTime += gcBean.getCollectionTime();
        }
        return totalGarbageCollectionTime;
    }

    /**
     * Updates the memory usage statistics and writes them to a file if logging is enabled.
     *
     * @see #writeMemoryUsageToFile()
     */
    public void update() {
        long now = System.nanoTime();
        if (now - lastTime >= 1_000_000_000) { // One second has passed
            long currentMemoryUsed = getMemoryUsed();
            memoryUsedPerSecond = (currentMemoryUsed - lastMemoryUsed) / (1024.0 * 1024.0); // Convert to MB
            lastMemoryUsed = currentMemoryUsed;
            lastTime = now;

            if (loggingEnabled) {
                writeMemoryUsageToFile();
            }
        }
    }

    /**
     * Returns the memory usage per second in a formatted string.
     *
     * @return String
     */
    public String getMemoryUsagePerSecond() {
        return String.format("MPS: %.2f MB/s", memoryUsedPerSecond);
    }

    /**
     * Writes the memory usage statistics in the logs.log file along with other statistics.
     *
     * @throws IOException if an I/O error occurs (file writer)
     * @see #getHeapMemoryUsage()
     * @see #getNonHeapMemoryUsage()
     * @see #getGarbageCollectionTime()
     * @see #getMemoryUsagePerSecond()
     * @see #writeMemoryUsageToFile()
     * @see #logNum
     * @see #newSessionNotified
     * @see #targetFPS
     * @see #World.getFPS()
     * @see #System.currentTimeMillis()
     */
    private void writeMemoryUsageToFile() {
        if (logWriter == null) return;
        try {
            if (!newSessionNotified) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                logWriter.println("New Log Session @ " + now.format(formatter));
                newSessionNotified = true;
            }

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = currentTime.format(timeFormatter);
            MemoryUsage heapMemoryUsage = getHeapMemoryUsage();
            MemoryUsage nonHeapMemoryUsage = getNonHeapMemoryUsage();
            long gcTime = getGarbageCollectionTime();
            logWriter.println(logNum + " | " + formattedTime + " | " + getMemoryUsagePerSecond() + " | Heap: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB | Non-Heap: " + nonHeapMemoryUsage.getUsed() / (1024 * 1024) + " MB | GC Time: " + gcTime + " ms | FPS: " + World.getFPS() + " targeting " + targetFPS + " | Sys Time " + System.currentTimeMillis() + " | Interval " + World.getTicksDone());
            logWriter.flush();
            logNum++;
        } catch (Exception e) {
            ErrorLogger.reportException("9A", "IO.OUT", "writeMemoryUsageToFile()", e);
        }
    }

    /**
     * Closes the persistent log writer. Called from {@link World#stop()}.
     */
    public void close() {
        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (Exception ignored) {
            }
            logWriter = null;
        }
    }
}