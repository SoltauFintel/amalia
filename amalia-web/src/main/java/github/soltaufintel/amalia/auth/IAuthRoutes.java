package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.web.route.RouteHandler;
import github.soltaufintel.amalia.web.route.Routes;

/**
 * You should call setupAuthFilter() in routes() !
 */
public interface IAuthRoutes extends Routes {

    RouteHandler getLoginPageRouteHandler();
}
