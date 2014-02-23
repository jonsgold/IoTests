package test.performance.nio;

import test.performance.FileReader;
import test.performance.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;

/**
 * This class uses a NIO channel to read the text into a byte array,
 * and uses the same thread to process the content.
 * <p/>
 *
 * @author Jonathan Goldstein
 */
public class ChunkFileReader_WrappedByteArray extends FileReader {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final byte[] barray;
    private final ByteBuffer byteBuffer;
    private final Logger logger;

    public ChunkFileReader_WrappedByteArray(int aBufferSize, Logger aLogger) {
        barray = new byte[aBufferSize];
        byteBuffer = ByteBuffer.wrap(barray);
        logger = aLogger;

        logger.debug(Logger.Format.WITHOUT_TIME, "Buffer size: %s", aBufferSize);
    }

    /**
     * Reads the file in the InputStream argument and calculates the sum of all the digits.
     *
     * @param aStream the input
     *
     * @throws IOException if problem occurred reading from the InputStream
     */
    protected int process(InputStream aStream) throws IOException {
        int result = 0;

        ReadableByteChannel channel = Channels.newChannel(aStream);

        while (channel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            int limit = byteBuffer.limit();

            if (limit == barray.length) {
                int i = barray.length - 1;

                // Find first new line from end of array.
                while (i > 0 && barray[i] != '\n') {
                    i--;
                }

                limit = i;
            }

            byte[] barray2 = new byte[limit];
            byteBuffer.get(barray2);
            byteBuffer.mark();

            String s = new String(barray2, CHARSET);
            Matcher m = DIGIT_PATTERN.matcher(s);

            while (m.find()) {
                String digit = m.group();
                result += Integer.parseInt(digit);
            }

            // Remaining bytes after the last new line are saved for the next iteration.
            byteBuffer.compact();
        }

        logger.debug(Logger.Format.WITH_TIME, "Closing file");
        channel.close();

        return result;
    }
}
