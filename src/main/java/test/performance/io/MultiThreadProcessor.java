package test.performance.io;

import test.performance.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;

/**
 * This class processes the content by using multiple worker threads.
 * <p/>
 *
 * @author Jonathan Goldstein
 */
public class MultiThreadProcessor extends LineByLineFileReader {

    private final int numOfLines;

    private int counter;
    //private final int numOfThreads;

    private final ExecutorService executor;
    private final List<String> lines;
    private final List<Future<Integer>> workerResults;

    public MultiThreadProcessor(int aNumOfLines, Logger aLogger) {
        super(aLogger);
        numOfLines = aNumOfLines;
        //numOfThreads = Runtime.getRuntime().availableProcessors();

        logger.debug(Logger.Format.WITHOUT_TIME, "Number of lines: %s", numOfLines);
        //logger.debug(Logger.Format.WITHOUT_TIME, "Number of worker threads: %s", numOfThreads);

        executor = Executors.newCachedThreadPool();
        //executor = Executors.newFixedThreadPool(numOfThreads);
        lines = new ArrayList<>(numOfLines);
        workerResults = new ArrayList<>();
    }

    @Override
    int doProcess(int aLineNumber, String aLine) {
        lines.add(aLine);

        // Create a new worker every numOfLines lines.
        if ((aLineNumber % numOfLines) == 0) {
            logger.debug(Logger.Format.WITH_TIME, "Creating worker %s", counter);
            workerResults.add(executor.submit(new Worker(counter++, new ArrayList<>(lines), logger)));
            lines.clear();
        }

        return 0;
    }

    @Override
    int doPostProcess() {
        int result = 0;

        if (lines.size() > 0) {
            logger.debug(Logger.Format.WITH_TIME, "Creating last worker (number of lines = %s)", lines.size());
            workerResults.add(executor.submit(new Worker(counter, new ArrayList<>(lines), logger)));
        }

        logger.debug(Logger.Format.WITH_TIME, "Getting results");

        for (Future<Integer> future : workerResults) {
            try {
                result += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        logger.debug(Logger.Format.WITH_TIME, "Shutting down executor");
        executor.shutdown();

        return result;
    }

    private static class Worker implements Callable<Integer> {

        private final int counter;

        private final List<String> list;
        private final Logger logger;

        Worker(int aCounter, List<String> aList, Logger aLogger) {
            counter = aCounter;
            list = aList;
            logger = aLogger;
        }

        @Override
        public Integer call() throws ParseException {
            logger.debug(Logger.Format.WITH_TIME, "> %s: worker %s started", Thread.currentThread().getName(), counter);

            int result = 0;

            for (String s : list) {
                Matcher m = DIGIT_PATTERN.matcher(s);
                while (m.find()) {
                    String digit = m.group();
                    result += Integer.parseInt(digit);
                }
            }

            logger.debug(Logger.Format.WITH_TIME, "> %s: worker %s result = %s", Thread.currentThread().getName(), counter, result);

            return result;
        }
    }
}
