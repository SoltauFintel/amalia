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
     * @param loginPageHtml deliver HTML of login page
     */
    public Auth(AppConfig config, RememberMe rememberMe, int encryptionFrequency, LoginPageHtml loginPageHtml) {
        this(config, rememberMe, encryptionFrequency, AuthRoutes.class, loginPageHtml);
    }

    /**
     * @param config -
     * @param rememberMe -
     * @param encryptionFrequency secret value, usually between 7000 and 10000
     * @param routes -
     * @param loginPageHtml deliver HTML of login page
     */
    public Auth(AppConfig config, RememberMe rememberMe, int encryptionFrequency, Class<? extends IAuthRoutes> routes, LoginPageHtml loginPageHtml) {
        super(rememberMe, routes, loginPageHtml);
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
