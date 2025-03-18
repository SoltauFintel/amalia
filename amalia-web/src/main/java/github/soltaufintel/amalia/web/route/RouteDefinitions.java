package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.IEngine;

/**
 * Implement this class for defining routes
 */
public abstract class RouteDefinitions implements Routes {
	private final IEngine engine;
    private final IAuth auth;
    private final PageInitializer pageInit;

    public RouteDefinitions(IEngine engine, IAuth auth, PageInitializer pageInit) {
    	this.engine = engine;
    	this.auth = auth;
    	this.pageInit = pageInit;
    }
    
    @Override
    public int getPriority() {
        return 100;
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

    /**
     * get()+post()
     * @param path path
     * @param pageClass route class
     */
    public void form(String path, Class<? extends Route<?>> pageClass) {
        get(path, pageClass);
        post(path, pageClass);
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
}
