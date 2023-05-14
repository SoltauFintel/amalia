package github.soltaufintel.amalia.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import github.soltaufintel.amalia.auth.AuthException.MissingRoleException;
import github.soltaufintel.amalia.auth.AuthException.UnknownNotificationIdException;
import github.soltaufintel.amalia.auth.AuthException.UserDataInUnexpectedModeException;
import github.soltaufintel.amalia.auth.AuthException.UserDoesNotExistException;
import github.soltaufintel.amalia.auth.AuthException.WrongOldPasswordException;
import github.soltaufintel.amalia.auth.rememberme.NoOpRememberMe;
import github.soltaufintel.amalia.auth.webcontext.Session;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.mail.MailSender;
import github.soltaufintel.amalia.web.config.AppConfig;

/**
 * Forgot password, change own password, set password by admin.
 */
public class PasswordTest {
	private TestUser user, user2;
	private int updated = 0;

	@Before
	public void before() {
		MailSender.active = false;
	}

	@Test
	public void forgot() {
		// Prepare
		IAuthService sv = createAuthService();
		
		// Test
		sv.forgotPassword("scott@tiger.de");
		
		// Verify
		Assert.assertEquals("scott@tiger.de", MailSender.to);
		Assert.assertEquals("Passwort vergessen", MailSender.subject);
		Assert.assertEquals("http://localhost:8080/auth/rp?id=3", MailSender.body);
		Assert.assertEquals("V", user.getMode());
		Assert.assertEquals("3", user.getNotificationId());
		Assert.assertNotNull(user.getNotificationTimestamp());
		Assert.assertEquals(1, updated);

		// Test
		sv.checkForgottenPasswordNotificationId(RegisterTest.mailId());

		// Negative test
		try {
			sv.checkForgottenPasswordNotificationId("4");
			Assert.fail("UnknownNotificationIdException expected");
		} catch (UnknownNotificationIdException e) {
		}

		// Test
		updated = 0;
		String pw = user.getPassword();
		final String mailId = RegisterTest.mailId();
		sv.changeForgottenPassword(mailId, "puma");
		
		// Verify
		Assert.assertEquals("Changed password mail error!", "scott, you, testhost", MailSender.body);
		Assert.assertEquals(1, updated);
		Assert.assertNotEquals(pw, user.getPassword());
		Assert.assertNull(user.getMode());
		Assert.assertNull(user.getNotificationTimestamp());

		// Test
		try {
			sv.changeForgottenPassword(mailId, "puma");
			Assert.fail("UserDataInUnexpectedModeException expected");
		} catch (UserDataInUnexpectedModeException e) {
		}
	}

	@Test
	public void changePassword() {
		// Prepare
		IAuthService sv = createAuthService();
		LoginTest.initPassword(user);
		if (!sv.login("scott", "tiger")) {
			Assert.fail("precondition: login not possible");
		}
		try {

			// Test
			sv.changePassword("tiger", "puma");
			
			// Verify
			Assert.assertEquals("Changed password mail error!", "scott, you, testhost", MailSender.body);
			if (sv.login("scott", "tiger")) {
				Assert.fail("Old password still active!");
			} else {
				if (!sv.login("scott", "puma")) {
					Assert.fail("New password has not been set!");
				}
			}
		} finally {
			sv.logout();
		}
	}

	@Test(expected =  WrongOldPasswordException.class)
	public void changePassword_wrongOldPassword() {
		// Prepare
		IAuthService sv = createAuthService();
		LoginTest.initPassword(user);
		if (!sv.login("scott", "tiger")) {
			Assert.fail("precondition: login not possible");
		}
		try {

			// Test
			sv.changePassword("quatsch", "puma");
		} finally {
			sv.logout();
		}
	}

	@Test
	public void setPassword() {
		// Prepare
		IAuthService sv = createAuthService();
		// create 2nd user
		sv.register("foo", "bar12312_alw", "test@localhost.com");
		LoginTest.initPassword(user);
		user.getRoles().add(IUser.ADMIN_ROLE);
		// Login as admin
		if (!sv.login("scott", "tiger")) {
			Assert.fail("precondition: login not possible");
		}
		try {
			
			// Test
			sv.setPassword("22", "a");
		} finally {
			sv.logout();
		}
		
		// Verify
		Assert.assertEquals("Changed password mail error!", "foo, admin, testhost", MailSender.body);
		sv.login("foo", "a");
		sv.logout();
	}

	@Test(expected = MissingRoleException.class)
	public void setPassword_notAdmin() {
		// Prepare
		IAuthService sv = createAuthService();
		// create 2nd user
		sv.register("foo", "bar12312_alw", "test@localhost.com");
		LoginTest.initPassword(user);
		if (!sv.login("scott", "tiger")) {
			Assert.fail("precondition: login not possible");
		}
		try {
			
			// Test
			sv.setPassword("22", "a");
		} finally {
			sv.logout();
		}
	}

	@Test(expected = UserDoesNotExistException.class)
	public void setPassword_unknownUser() {
		// Prepare
		IAuthService sv = createAuthService();
		LoginTest.initPassword(user);
		user.getRoles().add(IUser.ADMIN_ROLE);
		// Login as admin
		if (!sv.login("scott", "tiger")) {
			Assert.fail("precondition: login not possible");
		}
		try {
			
			// Test
			sv.setPassword("23", "a");
		} finally {
			sv.logout();
		}
	}

	private IAuthService createAuthService() {
		user = new TestUser();
		user.setId("1");
		user.setLogin("scott");
		user.setMailAddress("scott@tiger.de");
		user.setLockState(UserLockState.UNLOCKED);
		updated = 0;
		TestSession session = new TestSession();
		return new AuthService(
			new TestUserService() {
				@Override
				public IUser byLogin(String login) {
					return "scott".equals(login) ? user : null;
				}
				
				@Override
				public IUser byId(String userId) {
					return "1".equals(userId) ? user : "22".equals(userId) ? user2 : null;
				}
				
				@Override
				public List<IUser> byMail(String mail) {
					List<IUser> ret = new ArrayList<>();
					if (user.getMailAddress().equals(mail)) {
						ret.add(user);
					}
					return ret;
				}
				
				@Override
				public IUser byNotificationId(String notificationId) {
					return "3".equals(notificationId) ? user : null;
				}
				
				@Override
				public IUser createUser(String login, String name, String mailAddress, UserLockState lockState) {
					user2 = new TestUser();
					user2.setId("22");
					user2.setLogin(login);
					user2.setName(name);
					user2.setMailAddress(mailAddress);
					user2.setLockState(lockState);
					user2.setCreated(now());
					return user2;
				}
				
				@Override
				public void insert(IUser user) {
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
					return "3";
				}
			},
			7001,
			new MinimalPasswordRules(),
			new NoOpRememberMe(),
			new WebContext(null) {
				@Override
				public Session session() {
					return session;
				}
				
				@Override
				public String ipAddress() {
					return "testhost";
				}
				
				@Override
				public void redirect(String url) {
				}
			},
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
