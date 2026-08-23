package github.soltaufintel.amalia.web.image;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

import org.pmw.tinylog.Logger;

public interface IBinaryDataLoader {

    BinaryData load();

    static void download(String fileUrl, Duration timeout, File destination) {
        try {
            long start = System.currentTimeMillis();
            destination.getParentFile().mkdirs();
            HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fileUrl)).timeout(timeout).GET().build();
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(destination.toPath(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
            if (response.statusCode() != 200) {
                throw new IOException("Download error! HTTP status: " + response.statusCode());
            } else {
                Logger.info("download time: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
