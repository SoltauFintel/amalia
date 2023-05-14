package github.soltaufintel.amalia.web.action;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;

public class GuruErrorPage extends AbstractGuruErrorPage implements ErrorPage {
    protected Exception exception;

    @Override
    public void setException(Exception exception) {
        this.exception = exception;
        if (Strings.isNullOrEmpty(exception.getMessage())) {
            setMsg(exception.getClass().getName());
        } else {
            setMsg(exception.getMessage());
        }
    }
    
    @Override
    protected void execute() {
    	Logger.error("Error rendering path \"" + ctx.path() + "\":");
    	if (exception == null) {
    		try {
				Logger.error(model.get("msg").toString());
			} catch (Exception e) {
				Logger.error("(no error message)");
			}
    	} else {
    		Logger.error(exception);
    	}
        ctx.status(500);
    }
}
