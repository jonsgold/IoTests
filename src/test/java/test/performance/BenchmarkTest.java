package test.performance;

import test.performance.io.MultiThreadProcessor;
import test.performance.io.SingleThreadProcessor;
import test.performance.nio.ChunkFileReader_WrappedByteArray;

import java.io.File;
import java.util.Properties;

/**
 * This class runs the benchmark test.
 * <p/>
 * Run from the IoTests directory:
 * <p/>
 * <code>java -cp target/classes:target/test-classes test.performance.BenchmarkTest [SINGLE|MULTI|NIO]</code>
 *
 */
public class BenchmarkTest {

    private static final int MB = 1000000;

    private static enum TestType {
        SINGLE, MULTI, NIO
    }

    public static void main(String[] args) throws Exception {
        TestType type = TestType.valueOf(args[0]);
        BenchmarkTest test = new BenchmarkTest();
        test.run(type);
    }

    private void run(TestType aType) throws Exception {
        Properties properties = new Properties();
        properties.load(new java.io.FileReader("src/main/resources/config.properties"));

        File file = new File(properties.getProperty("input-file"));
        boolean debug = Boolean.parseBoolean(properties.getProperty("debug"));
        int numOfIterations = Integer.parseInt(properties.getProperty("iterations"));
        int numOfLines = Integer.parseInt(properties.getProperty("multi-thread-processing-number-of-lines"));
        int bufferSize = Integer.parseInt(properties.getProperty("nio-buffer-size"));

        Runtime runtime = Runtime.getRuntime();
        long startTime = System.currentTimeMillis();
        Logger logger = new Logger(debug, startTime);

        logger.log(Logger.Format.WITHOUT_TIME, "* Beginning processing, memory in MB: [max = %s, free = %s, total = %s]",
                runtime.maxMemory() / MB, runtime.freeMemory() / MB, runtime.totalMemory() / MB);

        long result = 0;
        for (int i = 0; i < numOfIterations; i++) {
            switch (aType) {
                case SINGLE:
                    result += new SingleThreadProcessor(logger).process(file);
                    break;
                case MULTI:
                    result += new MultiThreadProcessor(numOfLines, logger).process(file);
                    break;
                case NIO:
                    result += new ChunkFileReader_WrappedByteArray(bufferSize, logger).process(file);
                    break;
            }
        }

        double avgElapsedTime = (System.currentTimeMillis() - startTime) / 1000.0 / numOfIterations;
        long avgResult = result / numOfIterations;

        logger.log(Logger.Format.WITHOUT_TIME, "* Finished processing, memory in MB: [max = %s, free = %s, total = %s]",
                runtime.maxMemory() / MB, runtime.freeMemory() / MB, runtime.totalMemory() / MB);
        logger.log(Logger.Format.WITHOUT_TIME, "\n\tIterations = %s\n\tAvg result = %s\n\tAvg elapsed time = %s sec\n", numOfIterations,
                avgResult, avgElapsedTime);
    }
}
