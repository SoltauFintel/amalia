package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.rememberme.RememberMeInMongoDB;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.web.config.AppConfig;

public class MongoBasedAuthPlugin extends Auth {
    private final UserService sv = new UserService();

    /**
     * constructor with RememberMeInMongoDB
     */
    public MongoBasedAuthPlugin(AppConfig config, int encryptionFrequency) {
        this(config, new RememberMeInMongoDB(), encryptionFrequency);
    }
    
    public MongoBasedAuthPlugin(AppConfig config, RememberMe rememberMe, int encryptionFrequency) {
        super(config, rememberMe, encryptionFrequency);
        WebContext.setCookieName(config);
    }

    @Override
    protected IUserService getUserService() {
        return sv;
    }
}
