package github.soltaufintel.amalia.auth.simple;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.auth.IAuthRoutes;
import github.soltaufintel.amalia.auth.pages.LoginPage;
import github.soltaufintel.amalia.auth.pages.LogoutAction;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.IEngine;
import github.soltaufintel.amalia.web.route.RouteDefinitions;
import github.soltaufintel.amalia.web.route.RouteHandler;

public class SimpleAuthRoutes extends RouteDefinitions implements IAuthRoutes {

    public SimpleAuthRoutes(IEngine engine, IAuth auth, PageInitializer pageInit) {
		super(engine, auth, pageInit);
	}

	@Override
    public void routes() {
        setupAuthFilter();
        get("/auth/logout", LogoutAction.class);
    }

    @Override
    public RouteHandler getLoginPageRouteHandler() {
        return getRouteHandler(LoginPage.class);
    }
}
