package ActiverseUtils;

/**
 * ErrorLogger - Centralized ACEHS formatting and logging utility.
 * Ensures consistent error output across ActiverseEngine and ActiverseUtils.
 *
 * @author Knivier
 * @version 1.4.1
 */
public final class ErrorLogger {
    private ErrorLogger() {
    }

    /**
     * Logs a formatted ACEHS message to standard output.
     *
     * @param fileCode Source/system code (e.g. module identifier)
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param message Human-readable error details
     */
    public static void report(String fileCode, String errorType, String methodSignature, String message) {
        System.out.println(format(fileCode, errorType, methodSignature, message, null, null));
    }

    /**
     * Logs a formatted ACEHS message with a connection target descriptor.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param message Human-readable error details
     * @param connFileCode Linked/connected source code
     * @param connErrorType Linked/connected error taxonomy code
     */
    public static void report(String fileCode, String errorType, String methodSignature, String message,
                              String connFileCode, String connErrorType) {
        System.out.println(format(fileCode, errorType, methodSignature, message, connFileCode, connErrorType));
    }

    /**
     * Logs a formatted ACEHS message to standard error.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param message Human-readable error details
     */
    public static void reportErr(String fileCode, String errorType, String methodSignature, String message) {
        System.err.println(format(fileCode, errorType, methodSignature, message, null, null));
    }

    /**
     * Logs an exception in ACEHS format to standard output.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param e Exception instance to format
     */
    public static void reportException(String fileCode, String errorType, String methodSignature, Exception e) {
        System.out.println(format(fileCode, errorType, methodSignature, exceptionMessage(e), null, null));
    }

    /**
     * Logs an exception in ACEHS format including a connected code descriptor.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param e Exception instance to format
     * @param connFileCode Linked/connected source code
     * @param connErrorType Linked/connected error taxonomy code
     */
    public static void reportException(String fileCode, String errorType, String methodSignature, Exception e,
                                       String connFileCode, String connErrorType) {
        System.out.println(format(fileCode, errorType, methodSignature, exceptionMessage(e), connFileCode, connErrorType));
    }

    /**
     * Builds an ACEHS-formatted message without connected-code metadata.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param message Human-readable error details
     * @return Formatted ACEHS message
     */
    public static String format(String fileCode, String errorType, String methodSignature, String message) {
        return format(fileCode, errorType, methodSignature, message, null, null);
    }

    /**
     * Builds a full ACEHS-formatted message.
     *
     * @param fileCode Source/system code
     * @param errorType Error taxonomy code
     * @param methodSignature Method or location identifier
     * @param message Human-readable error details
     * @param connFileCode Linked/connected source code, nullable
     * @param connErrorType Linked/connected error taxonomy code, nullable
     * @return Formatted ACEHS message
     */
    public static String format(String fileCode, String errorType, String methodSignature, String message,
                                String connFileCode, String connErrorType) {
        StringBuilder output = new StringBuilder();
        output.append(fileCode).append(".").append(errorType);
        if (connFileCode != null && connErrorType != null) {
            output.append("-CONNTO-").append(connFileCode).append(".").append(connErrorType);
        }
        output.append(":(LN: ").append(methodSignature)
                .append(" - ACEHS Error thrown; ")
                .append(message)
                .append(")");
        return output.toString();
    }

    private static String exceptionMessage(Exception e) {
        if (e == null) {
            return "Unknown exception.";
        }
        String msg = e.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            return e.getClass().getSimpleName();
        }
        return e.getClass().getSimpleName() + ": " + msg;
    }
}
