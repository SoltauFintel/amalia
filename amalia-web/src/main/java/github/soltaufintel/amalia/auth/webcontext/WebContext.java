package github.soltaufintel.amalia.auth.webcontext;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.config.AppConfig;
import github.soltaufintel.amalia.web.route.RouteHandler;
import spark.Request;
import spark.Response;

public class WebContext {
    private static String cookieName;
    private final Context ctx;
    private final Session session;
    private final Cookie cookie;

    public WebContext(Request req, Response res) {
        this(new Context(req, res));
    }

    public WebContext(Context ctx) {
        this.ctx = ctx;
        session = new Session(ctx);
        cookie = new Cookie(cookieName, ctx);
    }
    
    public String path() {
        return ctx.path();
    }
    
    public Session session() {
        return session;
    }
    
    public Cookie cookie() {
        return cookie;
    }
    
    public Object handle(RouteHandler handler) {
        return handler.handle(ctx);
    }
    
    public static void setCookieName(AppConfig config) {
        String appName = config.get("app.name");
        cookieName = "KNOWNUSERID" + appName;
    }

    public String ipAddress() {
        return ctx.req.ip();
    }
    
    public void redirect(String url) {
        ctx.redirect(url);
    }
    
    public Request req() {
        return ctx.req;
    }
    
    public Response res() {
        return ctx.res;
    }
}
