package test.performance.io;

import test.performance.Logger;

import java.util.regex.Matcher;

/**
 * This class processes the content in the same thread as the invoker.
 * <p/>
 *
 * @author Jonathan Goldstein
 */
public class SingleThreadProcessor extends LineByLineFileReader {

    public SingleThreadProcessor(Logger aLogger) {
        super(aLogger);
    }

    @Override
    int doProcess(int aLineNumber, String aLine) {
        int result = 0;

        Matcher m = DIGIT_PATTERN.matcher(aLine);
        while (m.find()) {
            String digit = m.group();
            result += Integer.parseInt(digit);
        }

        return result;
    }

    @Override
    int doPostProcess() {
        return 0;
    }
}
