package github.soltaufintel.amalia.web.builder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.config.AppConfig;
import github.soltaufintel.amalia.web.engine.Engine;

public class Banner {

    public void print(String appVersion, AppConfig config, Engine engine) {
        banner();
        System.out.println("v" + appVersion + " ready on port " + config.getPort());
        System.out.println("Configuration file: " + config.getFilename()
            + " | Log level: " + Logger.getLevel()
            + " | Mode: " + (config.isDevelopment() ? "development" : "production"));

        String info = getTimeInfo();
        if (info != null) {
            System.out.println(info);
        }
    }

    protected void banner() {
        try (InputStream is = getClass().getResourceAsStream("/banner.txt")) {
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
