package test.performance;

import java.io.*;
import java.util.regex.Pattern;

public abstract class FileReader {

    protected static final Pattern DIGIT_PATTERN = Pattern.compile("(\\d)");

    public int process(String aString) throws IOException {
        if (aString == null) {
            return 0;
        }

        return process(new ByteArrayInputStream(aString.getBytes()));
    }

    public int process(File aFile) throws IOException {
        if (aFile == null) {
            return 0;
        }

        return process(new FileInputStream(aFile));
    }

    protected abstract int process(InputStream aStream) throws IOException;
}
