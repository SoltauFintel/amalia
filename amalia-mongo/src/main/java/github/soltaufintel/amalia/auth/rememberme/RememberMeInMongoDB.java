package github.soltaufintel.amalia.auth.rememberme;

import github.soltaufintel.amalia.base.IdGenerator;

public class RememberMeInMongoDB extends AbstractRememberMe {

	@Override
	protected IKnownUserDAO dao() {
		return new KnownUserDAO();
	}

	@Override
	protected IKnownUser createKnownUser(String user, String userId) {
		KnownUser knownUser = new KnownUser();
		knownUser.setId(IdGenerator.genId());
		knownUser.setUser(user);
		knownUser.setUserId(userId);
        knownUser.setCreatedAt(new java.util.Date(System.currentTimeMillis()));
		return knownUser;
	}
}
