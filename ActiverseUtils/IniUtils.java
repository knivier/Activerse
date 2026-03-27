package ActiverseUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Minimal INI parser/writer for key=value config files.
 *
 * Supported:
 * - Comments starting with '#' or ';'
 * - Optional section headers like [system] (ignored by the parser)
 * - key=value pairs (whitespace trimmed)
 */
public class IniUtils {

    public static Properties loadIni(String filePath) {
        Properties props = new Properties();

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return props;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                if (trimmed.startsWith("#") || trimmed.startsWith(";")) continue;
                if (trimmed.startsWith("[") && trimmed.endsWith("]")) continue;

                int eq = trimmed.indexOf('=');
                if (eq < 0) continue;

                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if (key.isEmpty()) continue;

                props.setProperty(key, value);
            }
        } catch (IOException e) {
            ErrorLogger.reportException("INI", "IN.OUT.IO", "loadIni(String)", e);
        }

        return props;
    }

    public static boolean saveIni(String filePath, Properties props, String sectionHeader) {
        Path path = Paths.get(filePath);

        // Ensure parent exists when saving into subfolders.
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                ErrorLogger.reportException("INI", "OUT.IO", "saveIni(String)", e);
                return false;
            }
        }

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            if (sectionHeader != null && !sectionHeader.isEmpty()) {
                bw.write("[" + sectionHeader + "]");
                bw.newLine();
            }

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                bw.write(key + "=" + value);
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            ErrorLogger.reportException("INI", "OUT.IO", "saveIni(String)", e);
            return false;
        }
    }
}

