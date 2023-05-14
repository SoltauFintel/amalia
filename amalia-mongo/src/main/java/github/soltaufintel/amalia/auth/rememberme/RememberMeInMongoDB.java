package github.soltaufintel.amalia.auth.rememberme;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.mongo.IdGenerator;

public class RememberMeInMongoDB implements RememberMe {
	// https://stackoverflow.com/a/5083809/3478021

	protected final KnownUserDAO dao = new KnownUserDAO();

    @Override
    public void rememberMe(boolean rememberMeWanted, WebContext ctx, String user, String userId) {
        if (rememberMeWanted) {
            KnownUser knownUser = new KnownUser();
            knownUser.setId(IdGenerator.genId());
            knownUser.setUser(user);
            knownUser.setUserId(userId);
            knownUser.setCreatedAt(new java.util.Date(System.currentTimeMillis()));
            dao.save(knownUser);

            ctx.cookie().set(knownUser.getId(), "remember-me");
        } else {
            ctx.cookie().remove();
            dao.delete(userId);
        }
    }

    @Override
    public void forget(WebContext ctx, String userId) {
        ctx.cookie().remove();
        if (userId != null) {
            dao.delete(userId);
        }
    }

    @Override
    public IKnownUser getUserIfKnown(WebContext ctx) {
        String id = ctx.cookie().get();
        if (id == null) {
            return null;
        }
        KnownUser ku = dao.get(id);
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
}
