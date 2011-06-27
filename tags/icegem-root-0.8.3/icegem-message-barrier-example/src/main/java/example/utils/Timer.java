package example.utils;

/**
 * User: akondratyev
 */
public class Timer {
    private static long fromTime;

    public static void start() {
        fromTime = System.currentTimeMillis();
    }

    public static long estimate() {
        return System.currentTimeMillis() - fromTime;
    }
}
