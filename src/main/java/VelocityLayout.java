import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.util.Date;

public class VelocityLayout extends Layout {
    protected Template template;

    @Override
    public String format(LoggingEvent loggingEvent) {
        VelocityContext context = new VelocityContext();
        context.put("c", loggingEvent.getLoggerName());
        context.put("d", new Date(loggingEvent.getTimeStamp()));
        context.put("m", loggingEvent.getRenderedMessage());
        context.put("p", loggingEvent.getLevel());
        context.put("t", loggingEvent.getThreadName());
        context.put("n", Layout.LINE_SEP);

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);
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
