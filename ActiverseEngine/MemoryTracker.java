package ActiverseEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MemoryTracker {
    private final String propertiesFile = "Activerse.properties";
    private long lastTime;
    private long lastMemoryUsed;
    private double memoryUsedPerSecond;
    private boolean fileWriting;
    private int fps;

    public MemoryTracker() {
        loadProperties();
        lastTime = System.nanoTime();
        lastMemoryUsed = getMemoryUsed();
    }

    private void loadProperties() {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
            fileWriting = Boolean.parseBoolean(props.getProperty("file_writing", "false"));
            fps = Integer.parseInt(props.getProperty("fps", "60"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public void update() {
        long now = System.nanoTime();
        if (now - lastTime >= 1_000_000_000) { // One second has passed
            long currentMemoryUsed = getMemoryUsed();
            memoryUsedPerSecond = (currentMemoryUsed - lastMemoryUsed) / (1024.0 * 1024.0); // Convert to MB
            lastMemoryUsed = currentMemoryUsed;
            lastTime = now;

            if (fileWriting) {
                writeMemoryUsageToFile();
            }
        }
    }

    public String getMemoryUsagePerSecond() {
        return String.format("MUPS: %.2f MB/sec", memoryUsedPerSecond);
    }

    private void writeMemoryUsageToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("memory_usage.log", true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            writer.println(formattedDateTime + " | " + String.format("%.2f", memoryUsedPerSecond));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
