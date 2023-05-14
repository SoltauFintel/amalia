package github.soltaufintel.amalia.auth.rememberme;

import github.soltaufintel.amalia.auth.webcontext.WebContext;

public class NoOpRememberMe implements RememberMe {

	@Override
	public void rememberMe(boolean rememberMeWanted, WebContext ctx, String user, String userId) {
	}

	@Override
	public IKnownUser getUserIfKnown(WebContext ctx) {
		return null;
	}

	@Override
	public void forget(WebContext ctx, String userId) {
	}
}
