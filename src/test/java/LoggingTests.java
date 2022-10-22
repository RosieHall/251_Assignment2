import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

public class LoggingTests {
    private Logger logger;
    private MemAppender memAppender;

    @BeforeEach
    public void setUp() {
        logger = Logger.getLogger("TestLogger");
        memAppender = MemAppender.getInstance();
        // Set MemAppender back to defaults for each test
        memAppender.setEventList(null);
        memAppender.setLayout(null);
        memAppender.setMaxSize(0);
    }

    @AfterEach
    public void tearDown() {
        logger = null;
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
    public void testMemAppenderEventList() {
        memAppender.setLayout(new SimpleLayout());
        memAppender.setEventList(new ArrayList<>());
        memAppender.setMaxSize(10);
        logger.addAppender(memAppender);
        logger.info("Test");
        assertEquals(1, memAppender.getCurrentLogs().size());
        assertEquals("Test", memAppender.getCurrentLogs().get(0).getRenderedMessage());
        assertEquals(Level.INFO, memAppender.getCurrentLogs().get(0).getLevel());
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
}
