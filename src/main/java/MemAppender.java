import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collections;
import java.util.List;

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

    public void reset() {
        layout = null;
        eventList = null;
        maxSize = 0;
        numDiscarded = 0;
    }

    public long getDiscardedLogCount() {
        return numDiscarded;
    }

    public void setEventList(List<LoggingEvent> newList) {
        if (newList == null) {
            throw new IllegalArgumentException("Tried to set null event list on MemAppender");
        }

        eventList = newList;
    }

    public List<LoggingEvent> getCurrentLogs() {
        validateEventList();
        return Collections.unmodifiableList(eventList);
    }

    public List<String> getEventStrings() {
        validateEventList();
        validateLayout();
        return eventList.stream().map(e -> getLayout().format(e)).toList();
    }

    public void printLogs() {
        validateEventList();
        validateLayout();

        for (LoggingEvent e: eventList) {
            System.out.print(getLayout().format(e));
            if (getLayout().ignoresThrowable()) {
                String[] throwable = e.getThrowableStrRep();
                if (throwable != null) {
                    for (String s: throwable) {
                        System.out.println(s);
                    }
                }
            }
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
