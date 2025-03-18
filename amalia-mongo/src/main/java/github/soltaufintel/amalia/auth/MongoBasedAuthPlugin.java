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
    public MongoBasedAuthPlugin(AppConfig config, int encryptionFrequency, LoginPageHtml loginPageHtml) {
        this(config, new RememberMeInMongoDB(), encryptionFrequency, loginPageHtml);
    }
    
    public MongoBasedAuthPlugin(AppConfig config, RememberMe rememberMe, int encryptionFrequency, LoginPageHtml loginPageHtml) {
        super(config, rememberMe, encryptionFrequency, loginPageHtml);
        WebContext.setCookieName(config);
    }

    @Override
    public IUserService getUserService() {
        return sv;
    }
}
