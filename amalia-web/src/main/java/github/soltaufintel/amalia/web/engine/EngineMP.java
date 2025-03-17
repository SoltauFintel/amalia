package github.soltaufintel.amalia.web.engine;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.ErrorPage;
import github.soltaufintel.amalia.web.route.RouteHandler;
import spark.Service;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;

/**
 * Spark Kapselung.
 * Außer das nicht gekapselte Context Interface.
 * 
 * Multi port application
 */
public class EngineMP implements IEngine {
	private final Service s;
	
    public EngineMP(int port, String staticFilesFolder, String appName, boolean isDevelopment) {
        if (appName != null) {
            EmbeddedJettyFactory jetty = new EmbeddedJettyFactory().withCookieName("SESSIONID_" + appName);
            EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, jetty);
        }
        
        s = Service.ignite().port(port);
        
        s.staticFileLocation(staticFilesFolder);
        if (isDevelopment) {
            s.externalStaticFileLocation("src/main/resources/" + staticFilesFolder);
        }
    }
    
	@Override
	public int port() {
		return s.port();
	}

	@Override
    public void get(String path, RouteHandler handler) {
        s.get(path, new SparkRouteAdapter(handler));
    }
    
	@Override
    public void post(String path, RouteHandler handler) {
        s.post(path, new SparkRouteAdapter(handler));
    }

	@Override
    public void put(String path, RouteHandler handler) {
        s.put(path, new SparkRouteAdapter(handler));
    }

	@Override
    public void patch(String path, RouteHandler handler) {
        s.patch(path, new SparkRouteAdapter(handler));
    }

	@Override
    public void delete(String path, RouteHandler handler) {
        s.delete(path, new SparkRouteAdapter(handler));
    }

	@Override
    public <T extends Exception> void exception(RouteHandler handler) {
        s.exception(RuntimeException.class, (exception, req, res) -> {
            res.body((String) handler.handle(new Context(req, res), aRoute -> {
                if (aRoute instanceof ErrorPage errorPage) {
                    errorPage.setException(exception);
                }
            }));
        });
    }

	@Override
    public void error404(RouteHandler handler) {
        s.notFound((req, res) -> handler.handle(new Context(req, res)));
    }
    
	@Override
    public void setupAuthFilter(IAuth auth) {
        s.before((req, res) -> auth.filter(new WebContext(req, res)));
        Logger.debug("[" + s.port()+  "] Auth filter installed: " + auth.getClass().getName());
    }
}
