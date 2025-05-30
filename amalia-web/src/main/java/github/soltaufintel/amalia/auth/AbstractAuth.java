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
    private final Class<? extends IAuthRoutes> routes;
    private final LoginPageHtml loginPageHtml;
    
    public AbstractAuth(RememberMe rememberMe, Class<? extends IAuthRoutes> routes, LoginPageHtml loginPageHtml) {
        this.rememberMe = rememberMe;
        this.routes = routes;
        this.loginPageHtml = loginPageHtml;
    }

    @Override
    public final Class<? extends IAuthRoutes> getRoutes() {
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
        if (isProtected(path) && !isLoggedIn(ctx)) {
            if (checkRememberMe(ctx)) {
                return;
            }
            userIsNotLoggedIn(ctx, path);
        }
    }
    
    protected boolean isLoggedIn(WebContext ctx) {
        return ctx.session().isLoggedIn();
    }

    public boolean checkRememberMe(WebContext ctx) {
        IKnownUser knownUser = rememberMe.getUserIfKnown(ctx);
        if (knownUser != null) {
            ctx.session().setUserId(knownUser.getUserId());
            ctx.session().setLogin(knownUser.getUser());
            ctx.session().setLoggedIn(true);
            return true;
        }
        return false;
    }

    public void userIsNotLoggedIn(WebContext ctx, String path) {
        saveGoBackPath(ctx, path);
        haltToLoginPage(ctx);
    }
    
    public void haltToLoginPage(WebContext ctx) {
        Spark.halt(401, loginPageHtml.getHtml(ctx));
    }
    
    public interface LoginPageHtml {
    	
    	String getHtml(WebContext ctx);
    }

    public void saveGoBackPath(WebContext ctx, String path) {
        if (!"/login".equals(path)) {
            String qs = ctx.req().queryString();
            if (qs != null) {
                path += "?" + qs;
            }
            ctx.session().setGoBackPath(path); // Go back to this page after login
        }
    }
    
    public RememberMe getRememberMe() {
        return rememberMe;
    }
}
