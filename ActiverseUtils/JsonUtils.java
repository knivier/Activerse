package ActiverseUtils;

/**
 * Minimal JSON string escaping for hand-built JSON (e.g. {@link WorldManager} saves).
 */
public final class JsonUtils {

    private JsonUtils() {}

    /** Escapes {@code \ " newline tab carriage return} for a JSON string value. */
    public static void appendEscaped(StringBuilder sb, String str) {
        if (str == null) {
            return;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
    }

    /** Allocating escape; prefer {@link #appendEscaped} when appending to a buffer. */
    public static String escapeString(String str) {
        if (str == null || str.isEmpty()) {
            return str == null ? "" : str;
        }
        StringBuilder sb = new StringBuilder(str.length() + 8);
        appendEscaped(sb, str);
        return sb.toString();
    }
}
