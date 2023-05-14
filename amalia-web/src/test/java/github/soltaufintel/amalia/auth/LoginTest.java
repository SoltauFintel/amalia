package github.soltaufintel.amalia.auth;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import github.soltaufintel.amalia.auth.rememberme.NoOpRememberMe;
import github.soltaufintel.amalia.auth.webcontext.Session;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.web.config.AppConfig;

public class LoginTest {
	private TestUser user;
	private boolean rememberMeCalled = false;
	private boolean forgetCalled = false;
	
	@Test
	public void login() {
		// Prepare
		TestSession session = new TestSession();
		IAuthService sv = new AuthService(
			new TestUserService() {
				@Override
				public IUser byLogin(String login) {
					return "scott".equals(login) ? user : null;
				}
			},
			7001,
			new MinimalPasswordRules(),
			new NoOpRememberMe() {
				@Override
				public void rememberMe(boolean rememberMeWanted, WebContext ctx, String user, String userId) {
					rememberMeCalled = true;
				}
				
				@Override
				public void forget(WebContext ctx, String userId) {
					forgetCalled = true;
				}
			},
			new WebContext(null) {
				@Override
				public Session session() {
					return session;
				}
				
				@Override
				public void redirect(String url) {
				}
			},
			new AppConfig(new Properties(), null));

		user = new TestUser();
		user.setId("1");
		user.setLogin("scott");
		user.setLockState(UserLockState.UNLOCKED);
		initPassword(user);

		// Test 1: success
		boolean ok = sv.login("scott", "tiger");
		
		// Verify 1
		Assert.assertTrue("login() must return true", ok);
		Assert.assertEquals("User must be logged in (session)", "yes", session.get("logged_in"));
		Assert.assertEquals("1", session.get("user_id"));
		Assert.assertEquals("scott", session.get("user"));
		Assert.assertTrue("rememberMe() must be called!", rememberMeCalled);
		Assert.assertFalse(forgetCalled);
		
		// Test 2: wrong password
		ok = sv.login("scott", "a");
		
		// Verify 2
		Assert.assertFalse("login() must return false", ok);
		
		// Test 3: unknown user
		ok = sv.login("nobody", "a");
		
		// Verify 3
		Assert.assertFalse("login() must return false", ok);

		// Test 4: user is locked
		user.setLockState(UserLockState.LOCKED);
		ok = sv.login("scott", "tiger");
		
		// Verify 4
		Assert.assertFalse("login() must return false", ok);

		// Test 5: user without activation
		user.setLockState(UserLockState.REGISTERED);
		ok = sv.login("scott", "tiger");
		
		// Verify 5
		Assert.assertFalse("login() must return false", ok);

		// Test 6: without salt
		user.setLockState(UserLockState.UNLOCKED);
		String salt = user.getSalt();
		user.setSalt("");
		ok = sv.login("scott", "tiger");
		
		// Verify 6
		Assert.assertFalse("login() must return false", ok);
		user.setSalt(salt); // restore

		// preconditions 7
		Assert.assertFalse(forgetCalled);
		Assert.assertNotNull(sv.getUserId());
		Assert.assertNotNull(sv.getLogin());

		// Test 7: logout
		sv.logout();
		
		// Verify 7
		Assert.assertNull("User must not be logged in (session)", session.get("logged_in"));
		Assert.assertNull(session.get("user_id"));
		Assert.assertNull(session.get("user"));
		Assert.assertTrue(forgetCalled);
		Assert.assertNull(sv.getUserId());
		Assert.assertNull(sv.getLogin());
	}

	static void initPassword(IUser user) {
		user.setSalt("r1293493285");
		user.setPassword("BD68E43F8A8029D62F8A920C71EF25BA");
	}
}
