package adventure;

/**
 * System logging and debugging outputs.
 * 
 * @author Marc Eberhard, Chris Cummins
 */
public class SystemIO {

    public static synchronized void log(String message) {
        final String now = new java.util.Date().toString();
        System.out.println(now + " OK:      " + message);
    }

    public static synchronized void warning(String message) {
        final String now = new java.util.Date().toString();
        System.err.println(now + " WARNING: " + message);
    }

    public static synchronized void error(String message) {
        final String now = new java.util.Date().toString();
        System.err.println(now + " ERROR:   " + message);
    }

    public static synchronized void error(String message, Exception e) {
        error(message);
        e.printStackTrace();
    }

    public static void fatal_error(String message) {
        error(message);
        System.exit(1);
    }

    public static void fatal_error(String message, Exception e) {
        error(message, e);
        System.exit(1);
    }
}
