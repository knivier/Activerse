package ActiverseUtils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtils - A utility class for common file operations such as reading, writing, appending,
 * checking existence, and creating directories.
 * This class is designed to simplify save/load systems, config management, and general file I/O.
 * It provides convenient methods with robust error handling and verbose comments.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class FileUtils {

    /**
     * ACESH error reporting utility.
     * @param fileCode The file code (e.g., "1B" for ActorVector.java)
     * @param errorType The error type (e.g., "LN", "IN", "OUT", etc.)
     * @param method The method where the error occurred
     * @param e The exception
     */
    private static void reportACESH(String fileCode, String errorType, String method, Exception e) {
        System.out.println(fileCode + "." + errorType + "." + method + " :(LN: " + method + "() - ACESH Error thrown; " + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
    }

    /**
     * Writes a string content to a file, overwriting if it exists.
     *
     * @param filePath The path to the file to write.
     * @param content  The string content to write to the file.
     * @return true if writing succeeded, false otherwise.
     */
    public static boolean writeFile(String filePath, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "writeFile", e);
            return false;
        }
    }

    /**
     * Appends a string content to the end of a file. Creates the file if it does not exist.
     *
     * @param filePath The path to the file to append to.
     * @param content  The string content to append.
     * @return true if append succeeded, false otherwise.
     */
    public static boolean appendToFile(String filePath, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "appendToFile", e);
            return false;
        }
    }

    /**
     * Reads all lines from a file into a List of Strings.
     *
     * @param filePath The path to the file to read.
     * @return List of lines if successful, empty list if file not found or error occurs.
     */
    public static List<String> readAllLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            reportACESH("2B", "IN", "readAllLines", e);
            return new ArrayList<>();
        }
    }

    /**
     * Reads the entire content of a file as a single String.
     *
     * @param filePath The path to the file.
     * @return File content as String, or empty string if error occurs.
     */
    public static String readFileAsString(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            reportACESH("2B", "IN", "readFileAsString", e);
            return "";
        }
    }

    /**
     * Checks if a file or directory exists at the given path.
     *
     * @param path The path to check.
     * @return true if the file/directory exists, false otherwise.
     */
    public static boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * Creates a directory (including parent directories) if it does not already exist.
     *
     * @param dirPath The directory path to create.
     * @return true if the directory was created or already exists, false if an error occurred.
     */
    public static boolean createDirectories(String dirPath) {
        try {
            Files.createDirectories(Paths.get(dirPath));
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "createDirectories", e);
            return false;
        }
    }

    /**
     * Deletes a file at the given path.
     *
     * @param filePath The path to the file to delete.
     * @return true if file was deleted, false if file didn't exist or error occurred.
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            reportACESH("2B", "OUT", "deleteFile", e);
            return false;
        }
    }

    /**
     * Copies a file from source path to target path, overwriting if target exists.
     *
     * @param sourcePath The path of the source file.
     * @param targetPath The path where the file will be copied.
     * @return true if copy succeeded, false otherwise.
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        try {
            Files.copy(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "copyFile", e);
            return false;
        }
    }

    /**
     * Moves (or renames) a file from source path to target path.
     *
     * @param sourcePath The path of the source file.
     * @param targetPath The new path or filename.
     * @return true if move succeeded, false otherwise.
     */
    public static boolean moveFile(String sourcePath, String targetPath) {
        try {
            Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "moveFile", e);
            return false;
        }
    }

    /**
     * Saves a list of strings to a file, overwriting the file.
     * Each string will be written as a separate line.
     *
     * @param filePath The file to save to.
     * @param lines    The lines to save.
     * @return true if save succeeded, false otherwise.
     */
    public static boolean saveLinesToFile(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "saveLinesToFile", e);
            return false;
        }
    }

    /**
     * Reads a list of strings from a file, where each line is an entry.
     *
     * @param filePath The file to read.
     * @return List of strings, or empty list if error occurs.
     */
    public static List<String> loadLinesFromFile(String filePath) {
        return readAllLines(filePath);
    }

    /**
     * Convenience method to clear (empty) a file by overwriting with an empty string.
     *
     * @param filePath The file to clear.
     * @return true if successful, false otherwise.
     */
    public static boolean clearFile(String filePath) {
        return writeFile(filePath, "");
    }

    /**
     * Reads a file containing key=value pairs into a java.util.Properties object.
     * Useful for configs or constants stored as properties.
     *
     * @param filePath The properties file to load.
     * @return Properties object loaded from file, or empty Properties if error occurs.
     */
    public static java.util.Properties loadProperties(String filePath) {
        java.util.Properties props = new java.util.Properties();
        try (InputStream input = Files.newInputStream(Paths.get(filePath))) {
            props.load(input);
        } catch (IOException e) {
            reportACESH("2B", "IN", "loadProperties", e);
        }
        return props;
    }

    /**
     * Saves a java.util.Properties object to a file.
     *
     * @param filePath   The file to save to.
     * @param properties The Properties object to save.
     * @param comments   Comments to include at the top of the properties file.
     * @return true if save succeeded, false otherwise.
     */
    public static boolean saveProperties(String filePath, java.util.Properties properties, String comments) {
        try (OutputStream output = Files.newOutputStream(Paths.get(filePath))) {
            properties.store(output, comments);
            return true;
        } catch (IOException e) {
            reportACESH("2B", "OUT", "saveProperties", e);
            return false;
        }
    }
}
