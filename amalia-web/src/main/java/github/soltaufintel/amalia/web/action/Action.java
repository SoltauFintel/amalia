package github.soltaufintel.amalia.web.action;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.IAuthService;
import github.soltaufintel.amalia.web.route.Route;

public abstract class Action extends Route<String> {

	@Override
	protected String render() {
		return "";
	}
	
	/**
	 * Shortcut to Escaper
	 * @param text any text
	 * @return HTML escaped text
	 */
	protected String esc(String text) {
		return Escaper.esc(text);
    }
    
	/**
     * Shortcut to Escaper
	 * @param text any text
	 * @return url encoded text
	 */
    protected String u(String text) {
        return Escaper.urlEncode(text, "");
    }

    public String getUserId() {
    	return auth().getUserId();
    }
    
    public String getLogin() {
    	return auth().getLogin();
    }
    
    public IAuthService auth() {
    	return Auth.auth.getService(ctx);
    }
}
