package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.action.GuruError404Page;
import github.soltaufintel.amalia.web.action.GuruErrorPage;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.IEngine;

public class ExceptionRouteDefinition extends RouteDefinitions {
    
    public ExceptionRouteDefinition(IEngine engine, IAuth auth, PageInitializer pageInit) {
    	super(engine, auth, pageInit);
    }

    /**
     * @return does usually implement ErrorPage interface
     */
    public Class<? extends Action> getErrorPage() {
    	return GuruErrorPage.class;
    }

    public Class<? extends Action> getError404Page() {
    	return GuruError404Page.class;
    }

    @Override
    public void routes() {
        exception(getErrorPage());
        error404(getError404Page());
    }
    
    @Override
    public int getPriority() {
    	return 900;
    }
}
