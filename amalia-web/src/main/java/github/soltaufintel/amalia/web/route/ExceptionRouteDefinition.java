package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.web.action.Action;

public class ExceptionRouteDefinition extends RouteDefinitions {
	private final Class<? extends Action> errorPage;
	private final Class<? extends Action> error404Page;
	
	/**
	 * @param errorPage does usually implement ErrorPage interface
	 * @param error404Page -
	 */
	public ExceptionRouteDefinition(Class<? extends Action> errorPage, Class<? extends Action> error404Page) {
		super(900);
		this.errorPage = errorPage;
		this.error404Page = error404Page;
	}

	@Override
	public void routes() {
		exception(errorPage);
		error404(error404Page);
	}
}
