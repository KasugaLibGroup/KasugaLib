package kasuga.lib.core.util;

import java.io.PrintStream;
import java.util.Base64;

public class StackTraceUtil {
    public static String getStackTraceString() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            builder.append(element.toString()).append("\n");
        }
        return builder.toString();
    }

    public static String writeStackTrace() {
        return Base64.getEncoder().encodeToString(getStackTraceString().getBytes());
    }
}
