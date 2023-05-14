package github.soltaufintel.amalia.auth;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import github.soltaufintel.amalia.auth.AuthException.ActivationTooLateException;
import github.soltaufintel.amalia.auth.AuthException.InvalidLoginStringException;
import github.soltaufintel.amalia.auth.AuthException.InvalidMailAddressException;
import github.soltaufintel.amalia.auth.AuthException.LoginAlreadyExistsException;
import github.soltaufintel.amalia.auth.AuthException.PasswordRulesViolationException;
import github.soltaufintel.amalia.auth.AuthException.UserDataInUnexpectedModeException;
import github.soltaufintel.amalia.auth.rememberme.NoOpRememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.mail.MailSender;
import github.soltaufintel.amalia.web.config.AppConfig;

public class RegisterTest {
	private TestUser user;
	private boolean insertedUser;
	private int updated = 0;

	@Before
	public void before() {
		MailSender.active = false;
	}
	
	@Test
	public void register() {
		// Prepare
		IAuthService sv = createAuthService();

		// Test
		sv.register("scott", "tiger", "test@localhost.dev");
		
		// Verify
		Assert.assertTrue(insertedUser);
		Assert.assertEquals(UserLockState.REGISTERED, user.getLockState());
		Assert.assertEquals(MailSender.to, "test@localhost.dev");
		Assert.assertEquals(MailSender.subject, "Registrierung");
		Assert.assertEquals(MailSender.body, "http://localhost:8080/auth/rm?id=2");
		Assert.assertFalse(user.getPassword().isEmpty());
		Assert.assertFalse(user.getSalt().isEmpty());
		Assert.assertNotNull(user.getMode());
		Assert.assertNotNull(user.getNotificationId());
		Assert.assertNotNull(user.getNotificationTimestamp());
		Assert.assertEquals(1, updated);
		
		// Ack register mail
		updated = 0;
		sv.registerUnlock(mailId());
		
		// Verify
		Assert.assertEquals(UserLockState.UNLOCKED, user.getLockState());
		Assert.assertNull(user.getMode());
		Assert.assertNull(user.getNotificationId());
		Assert.assertNull(user.getNotificationTimestamp());
		Assert.assertEquals(1, updated);
	}

	public static String mailId() {
		return MailSender.body.substring(MailSender.body.indexOf("id=") + "id=".length());
	}

	@Test(expected = InvalidLoginStringException.class)
	public void register_illegalLogin() {
		// Prepare
		IAuthService sv = createAuthService();

		// Test
		sv.register("Uschi Meyer", "tiger", "test@localhost.dev");
	}

	@Test(expected = PasswordRulesViolationException.class)
	public void register_emptyPassword() {
		// Prepare
		IAuthService sv = createAuthService();

		// Test
		sv.register("Uschi", "", "test@localhost.dev");
	}

	@Test(expected = InvalidMailAddressException.class)
	public void register_mailNull() {
		// Prepare
		IAuthService sv = createAuthService();

		// Test
		sv.register("Uschi", "tiger", null);
	}

	@Test(expected = InvalidMailAddressException.class)
	public void register_illegalMail() {
		// Prepare
		IAuthService sv = createAuthService();

		// Test
		sv.register("Uschi", "tiger", "uschimeyeratlocalhostcom");
	}

	@Test(expected = ActivationTooLateException.class)
	public void tooLate() {
		// Prepare
		IAuthService sv = createAuthService();
		sv.register("scott", "tiger", "test@localhost.dev");
		user.setNotificationTimestamp("1980-12-31 23:59");
		
		// Test
		sv.registerUnlock(mailId());
	}

	@Test(expected = UserDataInUnexpectedModeException.class)
	public void locked() {
		// Prepare
		IAuthService sv = createAuthService();
		sv.register("scott", "tiger", "test@localhost.dev");
		
		// Test
		user.setLockState(UserLockState.LOCKED); // admin has locked user
		sv.registerUnlock(mailId());
	}

	@Test(expected = LoginAlreadyExistsException.class)
	public void loginAlreadyTaken() {
		// Prepare
		IAuthService sv = createAuthService();
		sv.register("scott", "tiger", "test@localhost.dev");

		// Test
		sv.register("scott", "tiger2", "test2@localhost.dev");
	}

	private IAuthService createAuthService() {
		user = null;
		insertedUser = false;
		updated = 0;
		return new AuthService(
			new TestUserService() {
				@Override
				public IUser byLogin(String login) {
					return "scott".equals(login) ? user : null;
				}
				
				@Override
				public IUser byNotificationId(String notificationId) {
					return "2".equals(notificationId) ? user : null;
				}
				
				@Override
				public IUser createUser(String login, String name, String mailAddress, UserLockState lockState) {
					user = new TestUser();
					user.setId("1");
					user.setLogin(login);
					user.setName(name);
					user.setMailAddress(mailAddress);
					user.setLockState(lockState);
					user.setCreated(now());
					return user;
				}
				
				@Override
				public void insert(IUser user) {
					insertedUser = true;
				}
				
				@Override
				public void update(IUser user) {
					updated++;
				}
				
				@Override
				public void delete(String userId) {
					if (user != null && user.getId().equals(userId)) {
						user = null;
					}
				}
				
				@Override
				public String generateNotificationId() {
					return "2";
				}
			},
			7001,
			new MinimalPasswordRules(),
			new NoOpRememberMe(),
			new WebContext(null),
			new AppConfig(new Properties(), null) {
				@Override
				public String get(String key, String pDefault) {
					if ("url".equals(key)) {
						return "http://localhost:8080";
					} else if ("register-mail.max-time".equals(key)) {
						return "1";
					}
					return super.get(key, pDefault);
				}
			});
	}
}
