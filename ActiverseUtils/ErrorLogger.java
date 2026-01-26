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

    public static void report(String fileCode, String errorType, String methodSignature, String message) {
        System.out.println(format(fileCode, errorType, methodSignature, message, null, null));
    }

    public static void report(String fileCode, String errorType, String methodSignature, String message,
                              String connFileCode, String connErrorType) {
        System.out.println(format(fileCode, errorType, methodSignature, message, connFileCode, connErrorType));
    }

    public static void reportErr(String fileCode, String errorType, String methodSignature, String message) {
        System.err.println(format(fileCode, errorType, methodSignature, message, null, null));
    }

    public static void reportException(String fileCode, String errorType, String methodSignature, Exception e) {
        System.out.println(format(fileCode, errorType, methodSignature, exceptionMessage(e), null, null));
    }

    public static void reportException(String fileCode, String errorType, String methodSignature, Exception e,
                                       String connFileCode, String connErrorType) {
        System.out.println(format(fileCode, errorType, methodSignature, exceptionMessage(e), connFileCode, connErrorType));
    }

    public static String format(String fileCode, String errorType, String methodSignature, String message) {
        return format(fileCode, errorType, methodSignature, message, null, null);
    }

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
