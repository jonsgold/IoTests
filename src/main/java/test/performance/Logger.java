package test.performance;

/**
 * This class logs messages for the test classes.
 * <p/>
 *
 * @author Jonathan Goldstein
 */
public class Logger {

    private final boolean debug;
    private final long startTime;

    public enum Format {
        WITH_TIME, WITHOUT_TIME
    }

    public Logger(boolean aFlag, long aStartTime) {
        debug = aFlag;
        startTime = aStartTime;
    }

    public void debug(Format aFormat, String aMessage, Object... args) {
        if (debug) {
            log(aFormat, aMessage, args);
        }
    }

    public void log(Format aFormat, String aMessage, Object... args) {
        String s = String.format(aMessage, args);
        switch (aFormat) {
            case WITH_TIME: {
                System.out.printf("%s [elapsed: %s ms]\n", s, System.currentTimeMillis() - startTime);
                break;
            }
            case WITHOUT_TIME: {
                System.out.println(s);
                break;
            }
        }
    }
}
