package github.soltaufintel.amalia.web.action;

import org.pmw.tinylog.Logger;

public class GuruError404Page extends AbstractGuruErrorPage {

    @Override
    protected void execute() {
        String msg = "Page not found. (404)";
        if (!"/fonts/glyphicons-halflings-regular.woff2".equals(ctx.path()) && !ctx.path().startsWith("/.")) {
            Logger.error("Error rendering path \"" + ctx.path() + (ctx.req.queryString() == null ? "" : ("?" + ctx.req.queryString())) + "\": " + msg);
        }
        ctx.status(404);
        setMsg(msg);
    }
}
