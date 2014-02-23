package test.performance.io;

import test.performance.FileReader;
import test.performance.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class uses a java.io.BufferedReader to read the text line by line.
 * Processing is implemented by subclassses.
 * <p/>
 *
 * @author Jonathan Goldstein
 */
public abstract class LineByLineFileReader extends FileReader {

    final Logger logger;

    public LineByLineFileReader(Logger aLogger) {
        logger = aLogger;
    }

    /**
     * Reads the file in the InputStream argument and calculates the sum of all the digits.
     *
     * @param aStream the file input
     *
     * @throws java.io.IOException if problem occurred reading from the InputStream
     */
    protected int process(InputStream aStream) throws IOException {
        int result = 0;
        int i = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(aStream))) {
            for (String line; (line = br.readLine()) != null; ) {
                result += doProcess(++i, line);
            }
        } finally {
            logger.debug(Logger.Format.WITH_TIME, "Closing file");
        }

        result += doPostProcess();

        return result;
    }

    abstract int doProcess(int aLineNumber, String aLine);
    abstract int doPostProcess();
}
