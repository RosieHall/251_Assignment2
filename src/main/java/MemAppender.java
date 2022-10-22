import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemAppender extends AppenderSkeleton {
    private static final MemAppender instance = new MemAppender();

    private List<LoggingEvent> eventList;
    private int maxSize;
    private long numDiscarded;

    private MemAppender() {

    }

    public static MemAppender getInstance() {
        return instance;
    }

    public void setMaxSize(int newSize) {
        maxSize = newSize;
        if (eventList != null) {
            discardExcessLogs();
        }
    }

    public long getDiscardedLogCount() {
        return numDiscarded;
    }

    public void setEventList(List<LoggingEvent> newList) {
        eventList = newList;
    }

    public List<LoggingEvent> getCurrentLogs() {
        validateEventList();
        return Collections.unmodifiableList(eventList);
    }

    public List<String> getEventStrings() {
        validateEventList();
        validateLayout();
        //return eventList.stream().map(e -> getLayout().format(e)).collect(Collectors.toList());
        return null; // TODO
    }

    public void printLogs() {
        validateEventList();
        validateLayout();

        for (LoggingEvent e: eventList) {
            System.out.println(getLayout().format(e));
        }

        eventList.clear();
    }

    private void discardExcessLogs() {
        validateEventList();

        while (eventList.size() > maxSize) {
            eventList.remove(0);
            numDiscarded++;
        }
    }

    private void validateLayout() {
        if (getLayout() == null) {
            throw new IllegalStateException("No layout set on MemAppender");
        }
    }

    private void validateEventList() {
        if (eventList == null) {
            throw new IllegalStateException("No event list set on MemAppender");
        }
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        validateEventList();
        eventList.add(loggingEvent);
        discardExcessLogs();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
