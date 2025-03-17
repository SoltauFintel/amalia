package github.soltaufintel.amalia.spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spark.Request;
import spark.Response;

public class Context {
    public final Request req;
    public final Response res;
    
    public Context(Request req, Response res) {
        this.req = req;
        this.res = res;
    }
    
    public String path() {
        return req.uri();
    }
    
    public int port() {
    	return req.port();
    }
    
    public String method() {
        return req.requestMethod();
    }
    
    public boolean isPOST() {
        return "POST".equals(req.requestMethod());
    }
    
    public void redirect(String url) {
        res.redirect(url);
    }
    
    public void redirectToReferer() {
        redirect(req.headers("Referer"));
    }
    
    public String pathParam(String key) {
        return req.params(key);
    }
    
    public String queryParam(String key) {
        return req.queryParams(key);
    }
    
    /**
     * Values are separated with ","
     * @param name name of query parameter
     * @return values as List
     */
    public List<String> queryParamToList(String name) {
        List<String> ret = new ArrayList<>();
        String list = req.queryParams(name);
        if (list != null && !list.isBlank()) {
            for (String value : list.split(",")) {
                ret.add(value);
            }
        }
        return ret;
    }

    public List<String> queryParamsValues(String key) {
        String[] a = req.queryParamsValues(key);
        return a == null ? null : Arrays.asList(a);
    }
    
    public String formParam(String key) {
        return req.queryParams(key);
    }
    
    public void status(int status) {
        res.status(status);
    }

    public String body() {
        return req.body();
    }

    /**
     * @param name name of session attribute
     * @return value of session attribute
     */
    public String session(String name) {
        return req.session().attribute(name);
    }
    
    /**
     * @param name name of session attribute
     * @param value value of session attribute
     */
    public void session(String name, String value) {
        req.session().attribute(name, value);
    }

    /**
     * Sets header Content-Type = text/csv
     */
    public void contentTypeCSV() {
        res.header("Content-Type", "text/csv");
    }

    /**
     * Sets header Content-Type = application/json
     */
    public void contentTypeJSON() {
        res.header("Content-Type", "application/json");
    }

    /**
     * Sets header Content-Type = application/pdf
     */
    public void contentTypePDF() {
        res.header("Content-Type", "application/pdf");
    }

    /**
     * Sets header Content-Type = image/png
     */
    public void contentTypePNG() {
        res.header("Content-Type", "image/png");
    }

    /**
     * Sets header Content-Type = application/zip
     */
    public void contentTypeZip() {
        res.header("Content-Type", "application/zip");
    }
    
    public enum ContentDisposition {
        // lowercase!
        /** Opens file in browser */
        inline,
        /** Shows file in downloads */
        attachment;
    }
    
    /**
     * Setes header Content-Disposition
     * @param contentDisposition inline or attachment
     * @param filename without path
     */
    public void contentDisposition(ContentDisposition contentDisposition, String filename) {
        res.header("Content-Disposition", contentDisposition.name() + (filename == null || filename.isBlank() ? "" : "; filename=\"" + filename + "\""));
    }
    
    public void attachment(String filename) {
        contentDisposition(ContentDisposition.attachment, filename);
    }

    public void inline(String filename) {
        contentDisposition(ContentDisposition.inline, filename);
    }

    /**
     * Sets header Cache-Control = max-age= minutes * 60
     * @param minutes -
     */
    public void maxAge(int minutes) {
        res.header("Cache-Control", "max-age=" + (minutes * 60));
    }
}
