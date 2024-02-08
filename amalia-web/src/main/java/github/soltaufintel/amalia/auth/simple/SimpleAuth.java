package github.soltaufintel.amalia.auth.simple;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.IUserService;
import github.soltaufintel.amalia.auth.rememberme.NoOpRememberMe;
import github.soltaufintel.amalia.web.config.AppConfig;

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
}
