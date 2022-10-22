import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Date;

public class VelocityLayout extends Layout {
    private static final String DEFAULT_PATTERN = "$m$n";
    protected String pattern = DEFAULT_PATTERN;

    public VelocityLayout() {
    }

    public VelocityLayout(String pattern) {
        setPattern(pattern);
    }

    public void setPattern(String newPattern) {
        if (newPattern == null) {
            throw new IllegalArgumentException("Tried to set null pattern on VelocityLayout");
        }

        pattern = newPattern;
    }

    @Override
    public String format(LoggingEvent loggingEvent) {
        VelocityContext context = new VelocityContext();
        context.put("c", loggingEvent.getLoggerName());
        context.put("d", new Date(loggingEvent.getTimeStamp()).toString());
        context.put("m", loggingEvent.getRenderedMessage());
        context.put("p", loggingEvent.getLevel().toString());
        context.put("t", loggingEvent.getThreadName());
        context.put("n", Layout.LINE_SEP);

        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(context, stringWriter, "", pattern);
        return stringWriter.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return true;
    }

    @Override
    public void activateOptions() {
    }
}
