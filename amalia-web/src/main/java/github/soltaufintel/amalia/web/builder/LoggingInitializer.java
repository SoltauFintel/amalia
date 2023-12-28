package github.soltaufintel.amalia.web.builder;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

public class LoggingInitializer {
    private final Level defaultLevel;
    private final String formatPattern;
    
    public LoggingInitializer(Level level) {
        this(level, "{date}  {message}");
    }

    /**
     * @param defaultLevel -
     * @param formatPattern e.g. "{date} {level}  {message}"
     */
    public LoggingInitializer(Level defaultLevel, String formatPattern) {
        this.defaultLevel = defaultLevel;
        this.formatPattern = formatPattern;
    }

    public void init() {
        Level level;
        try {
            level = Level.valueOf(System.getenv("LOGLEVEL").toUpperCase());
        } catch (Exception ignore) {
            level = defaultLevel;
        }
        Configurator.currentConfig()
                .writer(new ConsoleWriter())
                .formatPattern(formatPattern)
                .level(level)
                .activate();
    }
}
