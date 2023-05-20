package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.config.AppConfig;

public abstract class Auth extends AbstractAuth {
	public static IAuth auth;
	private final int encryptionFrequency;
	private PasswordRules passwordRules = new MinimalPasswordRules();
	
	/**
	 * @param rememberMe -
	 * @param encryptionFrequency secret value, usually between 7000 and 10000
	 */
	public Auth(RememberMe rememberMe, int encryptionFrequency) {
		this(rememberMe, encryptionFrequency, new AuthRoutes(new AuthPages()));
	}

	/**
	 * @param rememberMe -
	 * @param encryptionFrequency secret value, usually between 7000 and 10000
	 * @param routes -
	 */
	public Auth(RememberMe rememberMe, int encryptionFrequency, IAuthRoutes routes) {
		super(rememberMe, routes);
		this.encryptionFrequency = encryptionFrequency;
	}
	
	@Override
	public IAuthService getService(Context ctx) {
		return new AuthService(getUserService(), encryptionFrequency, passwordRules,
				getRememberMe(), new WebContext(ctx), new AppConfig());
	}
	
	protected abstract IUserService getUserService();

	public PasswordRules getPasswordRules() {
		return passwordRules;
	}

	public void setPasswordRules(PasswordRules passwordRules) {
		this.passwordRules = passwordRules;
	}
}
