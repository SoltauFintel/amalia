package github.soltaufintel.amalia.web.route;

import java.util.function.Consumer;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.Page;
import github.soltaufintel.amalia.web.action.PageInitializer;

public class RouteHandler {
    public static boolean PAGE_INITIALIZER_FIRST = false; 
    private final PageInitializer pageInit;
    private final Class<? extends Route<?>> routeClass;
    
    /**
     * Standard constructor
     * @param pageInit class for initializing page (if action is a page)
     * @param routeClass route class. Class must have a default constructor.
     */
    public RouteHandler(PageInitializer pageInit, Class<? extends Route<?>> routeClass) {
        checkDefaultConstr(routeClass);
        this.pageInit = pageInit;
        this.routeClass = routeClass;
    }

    private void checkDefaultConstr(Class<? extends Route<?>> routeClass) {
        boolean hasDefaultConstr = false;
        try {
            hasDefaultConstr = routeClass.getDeclaredConstructor() != null;
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
        if (!hasDefaultConstr) {
            throw new RuntimeException(routeClass.getName() +
                    " must have a default constructor or use other RouteHandler constructor!");
        }
    }

    public Object handle(Context ctx) {
        return handle(ctx, aRoute -> {});
    }
    
    public Object handle(Context ctx, Consumer<Route<?>> extraInit) {
        // get route
        Route<?> route;
        try {
            route = routeClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        
        // init route
        if (PAGE_INITIALIZER_FIRST) {
            if (pageInit != null && route instanceof Page page) {
                pageInit.initPage(ctx, page);
            }
            route.init(ctx);
        } else {
            route.init(ctx);
            if (pageInit != null && route instanceof Page page) {
                pageInit.initPage(ctx, page);
            }
        }
        extraInit.accept(route);
        
        return route.run();
    }
}
