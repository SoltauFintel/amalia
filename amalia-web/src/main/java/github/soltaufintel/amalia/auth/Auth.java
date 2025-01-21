package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.config.AppConfig;

public abstract class Auth extends AbstractAuth {
    public static IAuth auth;
    private final AppConfig config;
    private final int encryptionFrequency;
    private PasswordRules passwordRules = new MinimalPasswordRules();
    
    /**
     * @param config -
     * @param rememberMe -
     * @param encryptionFrequency secret value, usually between 7000 and 10000
     */
    public Auth(AppConfig config, RememberMe rememberMe, int encryptionFrequency) {
        this(config, rememberMe, encryptionFrequency, new AuthRoutes(new AuthPages()));
    }

    /**
     * @param config -
     * @param rememberMe -
     * @param encryptionFrequency secret value, usually between 7000 and 10000
     * @param routes -
     */
    public Auth(AppConfig config, RememberMe rememberMe, int encryptionFrequency, IAuthRoutes routes) {
        super(rememberMe, routes);
        this.config = config;
        this.encryptionFrequency = encryptionFrequency;
    }
    
    @Override
    public IAuthService getService(Context ctx) {
        return new AuthService(getUserService(), encryptionFrequency, passwordRules,
                getRememberMe(), new WebContext(ctx), config);
    }
    
    public abstract IUserService getUserService();

    public PasswordRules getPasswordRules() {
        return passwordRules;
    }

    public void setPasswordRules(PasswordRules passwordRules) {
        this.passwordRules = passwordRules;
    }
}
