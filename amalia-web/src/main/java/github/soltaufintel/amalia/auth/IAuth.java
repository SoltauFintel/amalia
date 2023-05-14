package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;

public interface IAuth {

	IAuthService getService(Context ctx);
	
	IAuthRoutes getRoutes();
		
	void addNotProtected(String path);
	
	void filter(WebContext ctx);
}
