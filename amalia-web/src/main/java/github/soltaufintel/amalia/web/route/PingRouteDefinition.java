package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.IEngine;

public class PingRouteDefinition extends RouteDefinitions {

    public PingRouteDefinition(IEngine engine, IAuth auth, PageInitializer pageInit) {
		super(engine, auth, pageInit);
	}
    
    @Override
    public int getPriority() {
    	return 30;
    }

	@Override
    public void routes() {
        get("/rest/ping", PingAction.class);
        addNotProtected("/rest/ping");
        get("/rest/_ping", PingAction.class);
        addNotProtected("/rest/_");
    }
    
    public static class PingAction extends Action {

        @Override
        protected void execute() {
        }
        
        @Override
        protected String render() {
            return "pong";
        }
    }
}
