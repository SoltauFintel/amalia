package github.soltaufintel.amalia.spark;

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
    
    public String formParam(String key) {
        return req.queryParams(key);
    }
    
    public void status(int status) {
        res.status(status);
    }

    public String body() {
        return res.body();
    }
}
