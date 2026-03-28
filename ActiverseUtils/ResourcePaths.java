package ActiverseUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resolves game paths (e.g. {@code assets/images/foo.png}) to filesystem or classpath resources.
 */
public final class ResourcePaths {
    private ResourcePaths() {}

    static String normalizeClasspathKey(String path) {
        if (path == null) {
            return null;
        }
        String p = path.trim();
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        return p;
    }

    /**
     * Prefer an existing file; otherwise use a classpath URL (works for {@code java -jar}).
     */
    public static URL resolveUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String trimmed = path.trim();
        File f = new File(trimmed);
        if (f.isFile()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }
        String key = normalizeClasspathKey(trimmed);
        ClassLoader cl = ResourcePaths.class.getClassLoader();
        if (cl == null || key == null || key.isEmpty()) {
            return null;
        }
        return cl.getResource(key);
    }

    /**
     * Opens a stream for binary resources, preferring filesystem then classpath.
     */
    public static InputStream openInputStream(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IOException("path is null or empty");
        }
        String trimmed = path.trim();
        File f = new File(trimmed);
        if (f.isFile()) {
            return java.nio.file.Files.newInputStream(f.toPath());
        }
        String key = normalizeClasspathKey(trimmed);
        ClassLoader cl = ResourcePaths.class.getClassLoader();
        if (cl == null || key == null || key.isEmpty()) {
            throw new IOException("no class loader or empty path");
        }
        InputStream in = cl.getResourceAsStream(key);
        if (in == null) {
            throw new IOException("resource not found: " + path);
        }
        return in;
    }
}
