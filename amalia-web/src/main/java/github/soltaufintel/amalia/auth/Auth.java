package github.soltaufintel.amalia.auth;

import java.util.HashSet;
import java.util.Set;

import github.soltaufintel.amalia.auth.rememberme.IKnownUser;
import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.config.AppConfig;
import spark.Spark;

public abstract class Auth implements IAuth {
	public static IAuth auth;
	private final Set<String> notProtected = new HashSet<>();
	private final RememberMe rememberMe;
	private final int encryptionFrequency;
	private final IAuthRoutes routes;
	private PasswordRules passwordRules = new MinimalPasswordRules();
	
	public Auth(RememberMe rememberMe, int encryptionFrequency) {
		this(rememberMe, encryptionFrequency, new AuthRoutes(new AuthPages()));
	}

	public Auth(RememberMe rememberMe, int encryptionFrequency, IAuthRoutes routes) {
		this.rememberMe = rememberMe;
		this.encryptionFrequency = encryptionFrequency;
		this.routes = routes;
	}
	
	@Override
	public IAuthService getService(Context ctx) {
		return new AuthService(getUserService(), encryptionFrequency, passwordRules,
				rememberMe, new WebContext(ctx), new AppConfig());
	}
	
	protected abstract IUserService getUserService();

	public PasswordRules getPasswordRules() {
		return passwordRules;
	}

	public void setPasswordRules(PasswordRules passwordRules) {
		this.passwordRules = passwordRules;
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
}
