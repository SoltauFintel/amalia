package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.rememberme.RememberMeInMongoDB;
import github.soltaufintel.amalia.auth.simple.SimpleAuthRoutes;
import github.soltaufintel.amalia.auth.simple.SimpleUserService;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.web.config.AppConfig;

public class MongoSimpleAuth extends Auth {
    private final IUserService userService;

    /**
     * RememberMeInMongoDB, SimpleAuthRoutes, SimpleUserService, setCookieName
     * @param config config.getInt("encryption-frequency", 0)
     */
    public MongoSimpleAuth(AppConfig config) {
        super(config, new RememberMeInMongoDB(), config.getInt("encryption-frequency", 0), new SimpleAuthRoutes());
        userService = new SimpleUserService(config);
        WebContext.setCookieName(config);
    }

    @Override
    public IUserService getUserService() {
        return userService;
    }
}
