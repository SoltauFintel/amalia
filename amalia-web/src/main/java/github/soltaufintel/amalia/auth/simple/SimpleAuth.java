package github.soltaufintel.amalia.auth.simple;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.IUserService;
import github.soltaufintel.amalia.auth.rememberme.IKnownUser;
import github.soltaufintel.amalia.auth.rememberme.NoOpRememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.web.config.AppConfig;
import spark.Spark;

/**
 * Use in WebAppBuilder with <code>.withAuth(new SimpleAuth())</code>
 * and set encryption-frequency= in the AppConfig.properties with a secret value greater 7000.
 */
public class SimpleAuth extends Auth {
    private final IUserService userService = new SimpleUserService();

    public SimpleAuth() {
        super(new NoOpRememberMe(), new AppConfig().getInt("encryption-frequency", 0), new SimpleAuthRoutes());
    }

    @Override
    protected IUserService getUserService() {
        return userService;
    }
    
    @Override
    public void filter(WebContext ctx) {
        String path = ctx.path();
        if (isProtected(path) && !ctx.session().isLoggedIn()) {
            IKnownUser knownUser = getRememberMe().getUserIfKnown(ctx);
            if (knownUser != null) {
                ctx.session().setUserId(knownUser.getUserId());
                ctx.session().setLogin(knownUser.getUser());
                ctx.session().setLoggedIn(true);
                return;
            }
            if (!"/login".equals(path)) {
                ctx.session().setGoBackPath(path); // Go back to this page after login
            }
            Spark.halt(401, (String) ctx.handle(getRoutes().getLoginPageRouteHandler()));
        }
    }
}
