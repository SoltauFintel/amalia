package github.soltaufintel.amalia.web.builder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.config.AppConfig;

public class Banner {
    private final String filename;
    
    public Banner() {
        this("/banner.txt");
    }
    
    public Banner(String filename) {
        this.filename = filename;
    }
    
    public void print(String appVersion, AppConfig config) {
        banner();
		List<Integer> ports = config.getPorts();
		if (ports == null || ports.isEmpty()) {
			System.out.println("v" + appVersion + " ready on port " + config.getPort());
		} else {
			System.out.println("v" + appVersion + " ready on port" + (ports.size() == 1 ? "" : "s") + " "
					+ ports.stream().map(i -> i + "").collect(Collectors.joining(", ")));
		}
        System.out.println("Configuration file: " + config.getFilename()
            + " | Log level: " + Logger.getLevel()
            + " | Mode: " + (config.isDevelopment() ? "development" : "production"));

        String info = getTimeInfo();
        if (info != null) {
            System.out.println(info);
        }
    }

    protected void banner() {
        try (InputStream is = getClass().getResourceAsStream(filename)) {
            if (is == null) {
                return;
            }
            try (java.util.Scanner scanner = new java.util.Scanner(is)) {
                java.util.Scanner text = scanner.useDelimiter("\\A");
                if (text.hasNext()) {
                    System.out.println(text.next());
                }
            }
        } catch (IOException ignore) {
        }
    }
    
    protected String getTimeInfo() {
        return "Date/time: " + DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now())
                + ", timezone: " + ZoneId.systemDefault();
    }
    
    public void ready() {
        try {
            Thread.sleep(100); // Damit initExceptionHandler ggf. vorher ausgeführt werden kann.
            System.out.println("App loaded");
        } catch (InterruptedException e) { //
        }
    }
}
