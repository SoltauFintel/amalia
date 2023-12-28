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
    public MongoBasedAuthPlugin(int encryptionFrequency) {
        this(new RememberMeInMongoDB(), encryptionFrequency);
    }
    
    public MongoBasedAuthPlugin(RememberMe rememberMe, int encryptionFrequency) {
        super(rememberMe, encryptionFrequency);
        WebContext.setCookieName(new AppConfig());
    }

    @Override
    protected IUserService getUserService() {
        return sv;
    }
}
