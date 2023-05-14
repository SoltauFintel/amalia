package github.soltaufintel.amalia.auth;

/**
 * Validate login, password and mail address. Method throws exception in case of violation.
 */
public interface PasswordRules {

	void checkPassword(String password);
	
	void checkLogin(String login);
	
	void checkMailAddress(String mail);
}
