package github.soltaufintel.amalia.web.templating;

import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import com.github.template72.loader.TemplateLoader;

/**
 * NO CACHING FOR 'formular/' FILES!!!
 */
public class AmaliaTemplateFileCache implements TemplateLoader {
    protected final Map<String, String> cache = new HashMap<>();
    protected TemplateLoader parentLoader;

    @Override
    public String loadTemplate(String filename) {
        String template = null;
        if (!filename.startsWith("formular/")) {
            template = cache.get(filename);
        }
        if (template == null) {
            Logger.debug("load template: " + filename);
            template = parentLoader.loadTemplate(filename);
            cache.put(filename, template);
        }
        return template;
    }

    @Override
    public void setParentLoader(TemplateLoader parentLoader) {
        this.parentLoader = parentLoader;
    }
}
