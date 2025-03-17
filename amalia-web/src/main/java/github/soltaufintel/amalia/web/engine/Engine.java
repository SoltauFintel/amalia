package github.soltaufintel.amalia.web.engine;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.ErrorPage;
import github.soltaufintel.amalia.web.route.RouteHandler;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;

/**
 * Spark Kapselung.
 * Außer das nicht gekapselte Context Interface.
 */
public class Engine implements IEngine {
    
	public Engine(int port, String staticFilesFolder, String appName, boolean isDevelopment) {
        if (appName != null) {
            EmbeddedJettyFactory jetty = new EmbeddedJettyFactory().withCookieName("SESSIONID_" + appName);
            EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, jetty);
        }
        
        Spark.port(port);
        
        Spark.staticFileLocation(staticFilesFolder);
        if (isDevelopment) {
            Spark.externalStaticFileLocation("src/main/resources/" + staticFilesFolder);
        }
    }
	
	@Override
	public int port() {
		return Spark.port();
	}

    @Override
	public void get(String path, RouteHandler handler) {
        Spark.get(path, new SparkRouteAdapter(handler));
    }
    
    @Override
	public void post(String path, RouteHandler handler) {
        Spark.post(path, new SparkRouteAdapter(handler));
    }

    @Override
	public void put(String path, RouteHandler handler) {
        Spark.put(path, new SparkRouteAdapter(handler));
    }

    @Override
	public void patch(String path, RouteHandler handler) {
        Spark.patch(path, new SparkRouteAdapter(handler));
    }

    @Override
	public void delete(String path, RouteHandler handler) {
        Spark.delete(path, new SparkRouteAdapter(handler));
    }

    @Override
	public <T extends Exception> void exception(RouteHandler handler) {
        Spark.exception(RuntimeException.class, (exception, req, res) -> {
            res.body((String) handler.handle(new Context(req, res), aRoute -> {
                if (aRoute instanceof ErrorPage errorPage) {
                    errorPage.setException(exception);
                }
            }));
        });
    }

    @Override
	public void error404(RouteHandler handler) {
        Spark.notFound((req, res) -> handler.handle(new Context(req, res)));
    }
    
    @Override
	public void setupAuthFilter(IAuth auth) {
        Spark.before((req, res) -> auth.filter(new WebContext(req, res)));
        Logger.debug("Auth filter installed: " + auth.getClass().getName());
    }
}
