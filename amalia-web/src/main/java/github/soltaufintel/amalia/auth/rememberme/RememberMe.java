package github.soltaufintel.amalia.auth.rememberme;

import github.soltaufintel.amalia.auth.webcontext.WebContext;

/**
 * Remember me feature: User stays logged on during browser restart or web app
 * restart. User must log in only if he logged off or after many (e.g. 30) days.
 */
public interface RememberMe {

    /**
     * Remembers user. Used for login.
     * 
     * @param rememberMeWanted
     * @param ctx
     * @param user
     * @param userId
     */
    void rememberMe(boolean rememberMeWanted, WebContext ctx, String user, String userId);

    /**
     * Used for auto-login.
     * 
     * @param ctx
     * @return null if there's no known user
     */
    IKnownUser getUserIfKnown(WebContext ctx);

    /**
     * Forgets user. Used for logout.
     * 
     * @param ctx
     * @param userId contains service name and foreign user id
     */
    void forget(WebContext ctx, String userId);
}
