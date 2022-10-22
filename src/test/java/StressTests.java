import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;

public class StressTests {
    private void testLogger(Logger logger) {
        for (int i = 0; i < 100000; i++) {
            logger.info("TestLog" + i);
        }
    }

    @Nested
    public class MemAppenderTests {
        MemAppender appender;

        @BeforeEach
        public void setUp() {
            appender = MemAppender.getInstance();
            appender.reset();
        }

        @AfterEach
        public void tearDown() {
            appender = null;
        }

        @ParameterizedTest
        @ValueSource(ints = {10, 100, 1000})
        public void testMemAppenderArrayList(int maxSize) {
            MemAppender appender = MemAppender.getInstance();
            appender.setLayout(new SimpleLayout());
            appender.setEventList(new ArrayList<>());
            appender.setMaxSize(maxSize);

            Logger logger = Logger.getLogger("TestLogger");
            logger.addAppender(appender);

            testLogger(logger);
        }

        @ParameterizedTest
        @ValueSource(ints = {10, 100, 1000})
        public void testMemAppenderLinkedList(int maxSize) {
            MemAppender appender = MemAppender.getInstance();
            appender.setLayout(new SimpleLayout());
            appender.setEventList(new ArrayList<>());
            appender.setMaxSize(maxSize);

            Logger logger = Logger.getLogger("TestLogger");
            logger.addAppender(appender);

            testLogger(logger);
        }
    }

    @Test
    public void testConsoleAppender() {
        ConsoleAppender appender = new ConsoleAppender(new SimpleLayout());

        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);

        testLogger(logger);
    }

    @Test
    public void testFileAppender() throws IOException {
        FileAppender appender = new FileAppender(new SimpleLayout(), "test.txt");

        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);

        testLogger(logger);
    }
}
