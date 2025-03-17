package github.soltaufintel.amalia.web.engine;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.route.RouteHandler;

public interface IEngine {
	
	int port();

	void get(String path, RouteHandler handler);

	void post(String path, RouteHandler handler);

	void put(String path, RouteHandler handler);

	void patch(String path, RouteHandler handler);

	void delete(String path, RouteHandler handler);

	<T extends Exception> void exception(RouteHandler handler);

	void error404(RouteHandler handler);

	void setupAuthFilter(IAuth auth);
}