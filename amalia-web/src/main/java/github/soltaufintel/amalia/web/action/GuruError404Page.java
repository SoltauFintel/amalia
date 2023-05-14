package github.soltaufintel.amalia.web.action;

import org.pmw.tinylog.Logger;

public class GuruError404Page extends AbstractGuruErrorPage {

    @Override
    protected void execute() {
    	String msg = "Page not found. (404)";
    	Logger.error("Error rendering path \"" + ctx.path() + "\": " + msg);
        ctx.status(404);
        setMsg(msg);
    }
}
