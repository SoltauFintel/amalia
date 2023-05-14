package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.web.action.Action;

public class PingRouteDefinition extends RouteDefinitions {

	public PingRouteDefinition() {
		super(30);
	}
	
	@Override
	public void routes() {
		get("/rest/_ping", PingAction.class);
		addNotProtected("/rest/_");
	}
	
	public static class PingAction extends Action {

		@Override
		protected void execute() {
		}
		
		@Override
		protected String render() {
			return "ping";
		}
	}
}
