package github.soltaufintel.amalia.web.builder;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.writers.ConsoleWriter;

public class LoggingInitializer {
	private final Level defaultLevel;
	
	public LoggingInitializer(Level level) {
		this.defaultLevel = level;
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
                .formatPattern("{date}  {message}")
                .level(level)
                .activate();
	}
}
