package github.soltaufintel.amalia.web.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.commons.lang.StringEscapeUtils;
import org.pmw.tinylog.Logger;

public class Escaper {

    private Escaper() {
    }
    
    /** Escape HTML */
    public static String esc(String text) {
        return text == null ? "" : StringEscapeUtils.escapeHtml(text);
    }

    public static String toPrettyURL(String string) {
        if (string == null) {
            return string;
        }
        // https://stackoverflow.com/a/4581526/3478021
        return Normalizer.normalize(string.toLowerCase(), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }

    public static String urlEncode(String text, String fallback) {
        if (text == null) {
            return fallback;
        }
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error(e);
            return fallback;
        }
    }
}
