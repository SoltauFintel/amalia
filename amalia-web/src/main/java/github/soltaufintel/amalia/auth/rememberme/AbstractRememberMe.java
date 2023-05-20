package github.soltaufintel.amalia.auth.rememberme;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.webcontext.WebContext;

public abstract class AbstractRememberMe implements RememberMe {
	// https://stackoverflow.com/a/5083809/3478021
	
    @Override
    public void rememberMe(boolean rememberMeWanted, WebContext ctx, String user, String userId) {
        if (rememberMeWanted) {
            IKnownUser knownUser = createKnownUser(user, userId);
            dao().save(knownUser);

            ctx.cookie().set(knownUser.getId(), "remember-me");
        } else {
            ctx.cookie().remove();
            dao().delete(userId);
        }
    }

    @Override
    public void forget(WebContext ctx, String userId) {
        ctx.cookie().remove();
        if (userId != null) {
            dao().delete(userId);
        }
    }

    @Override
    public IKnownUser getUserIfKnown(WebContext ctx) {
        String id = ctx.cookie().get();
        if (id == null) {
            return null;
        }
        IKnownUser ku = dao().get(id);
        if (ku == null) {
        	ctx.cookie().remove();
        } else {
            logRememberedUser(ku.getUser(), ku.getUserId());
            ctx.cookie().extendLifeTime(ku.getId());
        }
        return ku;
    }

    protected void logRememberedUser(String user, String userId) {
        Logger.debug("Remembered user: " + user + " (" + userId + ")");
    }

	protected abstract IKnownUserDAO dao();

	protected abstract IKnownUser createKnownUser(String user, String userId);
}
