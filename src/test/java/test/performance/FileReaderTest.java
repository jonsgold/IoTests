package test.performance;

import org.junit.Before;
import org.junit.Test;
import test.performance.nio.ChunkFileReader_WrappedByteArray;

import static org.junit.Assert.assertEquals;

public class FileReaderTest {

    FileReader reader;

    @Before
    public void setup() {
        Logger logger = new Logger(false, 0L);
//        reader = new SingleThreadProcessor(logger);
//        reader = new MultiThreadProcessor(10, logger);
        reader = new ChunkFileReader_WrappedByteArray(1024, logger);
    }

    @Test
    public void testNullLog() throws Exception {
        String text = null;

        int result = reader.process(text);

        assertEquals("Result is not '0'.", 0, result);
    }

    @Test
    public void testEmptyLog() throws Exception {
        String text = "";

        int result = reader.process(text);

        assertEquals("Result is not '0'.", 0, result);
    }

    @Test
    public void testOneDigit() throws Exception {
        String text = "1";

        int result = reader.process(text);

        assertEquals("Result is not '1'.", 1, result);
    }

    @Test
    public void testOneDigit_TwoLines() throws Exception {
        String text = "1\n1";

        int result = reader.process(text);

        assertEquals("Result is not '2'.", 2, result);
    }
}
