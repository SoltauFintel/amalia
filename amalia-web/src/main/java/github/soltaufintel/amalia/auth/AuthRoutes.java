package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.route.RouteDefinitions;
import github.soltaufintel.amalia.web.route.RouteHandler;

public class AuthRoutes extends RouteDefinitions implements IAuthRoutes {
    private final AuthPages pages;
    
    public AuthRoutes(AuthPages pages) {
        this.pages = pages;
    }

    @Override
    public void routes() {
        setupAuthFilter();
        
        _public("/auth/register", pages.getRegisterPageClass());
        _public("/auth/registered", pages.getRegisteredPageClass());
        _public("/auth/forgot", pages.getForgotPasswordRequestPageClass());
        _public("/auth/rp", pages.getChangeForgottenPasswordPageClass()); // public link in mail, rp=reset-password
        get("/auth/rm", pages.getRegisterUnlockPageClass()); // public link in mail, rm=register-mail
        addNotProtected("/auth/rm");
        
        // Only for logged in user:
        
        get("/auth/logout", pages.getLogoutActionClass());
        _protected("/auth/user/current/change-password", pages.getChangeUserPasswordPageClass());
        _protected("/auth/user/:id/set-password", pages.getSetPasswordPageClass());
        
        get("/auth/user", pages.getUsersPageClass());
        _protected("/auth/user/:id/delete", pages.getDeleteUserPageClass());
        get("/auth/user/:id/lock", pages.getLockUserActionClass());
    }

    private void _public(String path, Class<? extends Action> pageClass) {
        get(path, pageClass);
        post(path, pageClass);
        addNotProtected(path);
    }

    private void _protected(String path, Class<? extends Action> pageClass) {
        get(path, pageClass);
        post(path, pageClass);
    }
    
    @Override
    public int getPriority() {
        return 60;
    }

    @Override
    public RouteHandler getLoginPageRouteHandler() {
        return getRouteHandler(pages.getLoginPageClass());
    }
}
