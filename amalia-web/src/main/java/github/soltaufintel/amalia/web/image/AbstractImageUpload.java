package github.soltaufintel.amalia.web.image;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Action;

/**
 * Foto Upload
 *
 * Man kann zwar mehrere Dateien auf einmal hochladen. Allerdings wird diese Action je Datei aufgerufen.
 */
public abstract class AbstractImageUpload extends Action {

    @Override
    protected void execute() {
        ctx.req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("upload"));
        try {
            Part part = ctx.req.raw().getPart(getId());
            String dn = getFileName(part).replace(" ", "-");
            Logger.info("Image upload: " + dn);
            saveImage(part.getInputStream(), dn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getId() {
        return "file";
    }
    
    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        throw new RuntimeException("Kein Dateiname!");
    }

    protected abstract void saveImage(InputStream content, String filename) throws IOException;
}
