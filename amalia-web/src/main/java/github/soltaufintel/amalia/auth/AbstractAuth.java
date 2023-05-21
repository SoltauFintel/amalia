package github.soltaufintel.amalia.auth;

import java.util.HashSet;
import java.util.Set;

import github.soltaufintel.amalia.auth.rememberme.IKnownUser;
import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import spark.Spark;

public abstract class AbstractAuth implements IAuth {
	private final Set<String> notProtected = new HashSet<>();
	private final RememberMe rememberMe;
	private final IAuthRoutes routes;
	
	public AbstractAuth(RememberMe rememberMe, IAuthRoutes routes) {
		this.rememberMe = rememberMe;
		this.routes = routes;
	}

	@Override
	public final IAuthRoutes getRoutes() {
		return routes;
	}

	@Override
	public void addNotProtected(String path) {
		notProtected.add(path);
	}

	protected boolean isProtected(String uri) {
        for (String begin : notProtected) {
            if (uri.startsWith(begin)) {
                return false;
            }
        }
        return true;
    }

	@Override
	public void filter(WebContext ctx) {
		String path = ctx.path();
		if (isProtected(path) && !ctx.session().isLoggedIn()) {
            IKnownUser knownUser = rememberMe.getUserIfKnown(ctx);
            if (knownUser != null) {
            	ctx.session().setUserId(knownUser.getUserId());
            	ctx.session().setLogin(knownUser.getUser());
            	ctx.session().setLoggedIn(true);
                return;
            }
            ctx.session().setGoBackPath(path); // Go back to this page after login
            Spark.halt(401, (String) ctx.handle(routes.getLoginPageRouteHandler()));
        }
	}
	
	public RememberMe getRememberMe() {
		return rememberMe;
	}
}
