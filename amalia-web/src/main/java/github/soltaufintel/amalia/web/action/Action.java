package github.soltaufintel.amalia.web.action;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.IAuthService;
import github.soltaufintel.amalia.web.route.Route;

public abstract class Action extends Route<String> {

	@Override
	protected String render() {
		return "";
	}
	
	protected String esc(String text) {
		return Escaper.esc(text);
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
