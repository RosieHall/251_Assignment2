import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingTests {
    private Logger logger;

    @BeforeEach
    public void setUp() {
        logger = Logger.getLogger("TestLogger");
    }

    @AfterEach
    public void tearDown() {
        logger = null;
    }

    @Nested
    public class MemAppenderTests {
        private MemAppender memAppender;

        @BeforeEach
        public void setUp() {
            memAppender = MemAppender.getInstance();
            // Set MemAppender back to default for each test
            memAppender.reset();
        }

        @AfterEach
        public void tearDown() {
            memAppender = null;
        }

        @Test
        public void testMemAppenderNoLayout() {
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> memAppender.getEventStrings());
            assertEquals("No layout set on MemAppender", e.getMessage());
        }

        @Test
        public void testMemAppenderNoEventList() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> logger.info("Test"));
            assertEquals("No event list set on MemAppender", e.getMessage());
        }

        @Test
        public void testMemAppenderSetNullEventList() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setMaxSize(10);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> memAppender.setEventList(null));
            assertEquals("Tried to set null event list on MemAppender", e.getMessage());
        }

        @Test
        public void testMemAppenderEventList() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");

            List<LoggingEvent> eventList = memAppender.getCurrentLogs();
            assertNotNull(eventList);
            assertEquals(1, eventList.size());
            assertEquals("Test", eventList.get(0).getRenderedMessage());
            assertEquals(Level.INFO, eventList.get(0).getLevel());
        }

        @Test
        public void testMemAppenderUnmodifiableEventList() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");

            assertThrows(UnsupportedOperationException.class, () -> memAppender.getCurrentLogs().clear());
        }

        @Test
        public void testMemAppenderStringList() {
            memAppender.setLayout(new PatternLayout("%p: %m"));
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");

            List<String> stringList = memAppender.getEventStrings();
            assertNotNull(stringList);
            assertEquals(1, stringList.size());
            assertEquals("INFO: Test", stringList.get(0));
        }

        @Test
        public void testMemAppenderUnmodifiableStringList() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");

            assertThrows(UnsupportedOperationException.class, () -> memAppender.getEventStrings().clear());
        }

        @Test
        public void testMemAppenderPrintLogs() {
            memAppender.setLayout(new PatternLayout("%p: %m"));
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);
            logger.info("Test");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            PrintStream stdout = System.out;
            System.setOut(new PrintStream(stream));

            try {
                memAppender.printLogs();
            } finally {
                System.setOut(stdout);
            }

            assertEquals(0, memAppender.getCurrentLogs().size());
            assertEquals("INFO: Test", stream.toString());
        }

        @Test
        public void testMemAppenderPrintLogsThrowable() {
            memAppender.setLayout(new PatternLayout("%p: %m%n"));
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(10);
            logger.addAppender(memAppender);

            logger.info("Test", new Exception("Exception"));

            LoggingEvent e = memAppender.getCurrentLogs().get(0);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            PrintStream stdout = System.out;
            System.setOut(new PrintStream(stream));

            try {
                memAppender.printLogs();
            } finally {
                System.setOut(stdout);
            }

            String expectedOutput = "INFO: Test" + Layout.LINE_SEP + String.join(Layout.LINE_SEP, e.getThrowableStrRep()) + Layout.LINE_SEP;
            assertEquals(expectedOutput, stream.toString());
        }

        @Test
        public void testMemAppenderDiscarding() {
            memAppender.setLayout(new SimpleLayout());
            memAppender.setEventList(new ArrayList<>());
            memAppender.setMaxSize(2);
            logger.addAppender(memAppender);

            logger.info("Test1");
            logger.info("Test2");
            assertEquals(0, memAppender.getDiscardedLogCount());

            logger.info("Test3");
            assertEquals(1, memAppender.getDiscardedLogCount());

            logger.info("Test4");
            assertEquals(2, memAppender.getDiscardedLogCount());
            assertEquals(2, memAppender.getCurrentLogs().size());

            assertEquals("Test3", memAppender.getCurrentLogs().get(0).getRenderedMessage());
            assertEquals("Test4", memAppender.getCurrentLogs().get(1).getRenderedMessage());
        }
    }

    @Nested
    public class VelocityLayoutTests {
        @Test
        public void testSetVelocityLayoutNullPattern() {
            VelocityLayout layout = new VelocityLayout();
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> layout.setPattern(null));
            assertEquals("Tried to set null pattern on VelocityLayout", e.getMessage());
        }

        @Test
        public void testVelocityLayoutFormat() {
            StringWriter writer = new StringWriter();
            WriterAppender appender = new WriterAppender();
            appender.setImmediateFlush(true);
            appender.setWriter(writer);
            appender.setLayout(new VelocityLayout("$p: $m"));
            logger.addAppender(appender);

            logger.info("Test");

            assertEquals("INFO: Test", writer.toString());
        }
    }
}
