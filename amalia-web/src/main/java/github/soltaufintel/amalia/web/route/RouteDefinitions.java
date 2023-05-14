package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.Engine;

/**
 * Implement this class for defining routes
 */
public abstract class RouteDefinitions implements Routes {
	private Engine engine;
	private IAuth auth;
	private PageInitializer pageInit;
	private final int priority;
	
	/**
	 * priority 100
	 */
	public RouteDefinitions() {
		this(100);
	}

	/**
	 * @param priority the higher the number the later the processing
	 */
	public RouteDefinitions(int priority) {
		this.priority = priority;
	}
	
	@Override
	public void init(Engine engine, IAuth auth, PageInitializer pageInit) {
		this.engine = engine;
		this.auth = auth;
		this.pageInit = pageInit;
	}

	protected void addNotProtected(String path) {
		auth.addNotProtected(path);
	}
	
	public void get(String path, Class<? extends Route<?>> routeClass) {
		engine.get(path, getRouteHandler(routeClass));
	}
	
	public void post(String path, Class<? extends Route<?>> routeClass) {
		engine.post(path, getRouteHandler(routeClass));
	}

	public void put(String path, Class<? extends Route<?>> routeClass) {
		engine.put(path, getRouteHandler(routeClass));
	}

	public void delete(String path, Class<? extends Route<?>> routeClass) {
		engine.delete(path, getRouteHandler(routeClass));
	}
	
	public <T extends Exception> void exception(Class<? extends Action> errorPage) {
        engine.exception(getRouteHandler(errorPage));
	}

	public void error404(Class<? extends Action> error404Page) {
		engine.error404(getRouteHandler(error404Page));
	}
	
	public RouteHandler getRouteHandler(Class<? extends Route<?>> routeClass) {
		return new RouteHandler(pageInit, routeClass);
	}
	
	public void setupAuthFilter() {
		engine.setupAuthFilter(auth);
	}

	@Override
	public int getPriority() {
		return priority;
	}
}
