package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.web.route.RouteHandler;
import github.soltaufintel.amalia.web.route.Routes;

public interface IAuthRoutes extends Routes {

	RouteHandler getLoginPageRouteHandler();
}
