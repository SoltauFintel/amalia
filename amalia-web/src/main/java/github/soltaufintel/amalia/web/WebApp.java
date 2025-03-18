package github.soltaufintel.amalia.web;

import static spark.Spark.initExceptionHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.auth.NoOpAuth;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.builder.Banner;
import github.soltaufintel.amalia.web.builder.Initializer;
import github.soltaufintel.amalia.web.builder.LoggingInitializer;
import github.soltaufintel.amalia.web.config.AppConfig;
import github.soltaufintel.amalia.web.engine.Engine;
import github.soltaufintel.amalia.web.engine.EngineMP;
import github.soltaufintel.amalia.web.engine.IEngine;
import github.soltaufintel.amalia.web.route.Routes;

public class WebApp {
    private final String appVersion;
    private final LoggingInitializer logging;
    private final AppConfig config;
    private final List<Initializer> initializers;
    private final List<Class<? extends Routes>> routes;
    private final PageInitializer pageInit;
    private final Banner banner;
    
    private List<IEngine> engines;

    public WebApp(String appVersion, LoggingInitializer logging, AppConfig config,
            List<Initializer> initializers, List<Class<? extends Routes>> routes, PageInitializer pageInit, Banner banner) {
        this.appVersion = appVersion;
        this.logging = logging;
        this.config = config;
        this.initializers = initializers;
        this.routes = routes;
        this.pageInit = pageInit;
        this.banner = banner;
    }

    public void boot() {
        logging.init();
        initExceptionHandler(ex -> {
            Logger.error("[FATAL] " + ex.getMessage());
            System.exit(100);
        });
        startEngine(config);
        banner.print(appVersion, config);
        initializers.forEach(i -> i.init(config));
        if (Auth.auth == null) {
            Logger.debug("Auth.auth is null and is set to NoOpAuth");
            Auth.auth = new NoOpAuth();
        }
        initRoutes();
        banner.ready();
    }

    protected void startEngine(AppConfig config) {
    	engines = new ArrayList<>();
    	String staticFiles = config.get("static-files", "web");
    	String appName = config.get("app.name");
    	List<Integer> ports = config.getPorts();
		if (ports == null || ports.isEmpty()) {
	        int port = config.getPort();
	        Logger.debug("single port application: " + port);
			engines.add(new Engine(port, staticFiles, appName, config.isDevelopment()));
    	} else {
    		for (Integer port : ports) {
				Logger.debug("multi-port application: " + port);
				engines.add(new EngineMP(port, staticFiles, appName, config.isDevelopment()));
				appName = null;
    		}
    	}
    }

	protected void initRoutes() {
		List<Routes> list = new ArrayList<>();
        for (Class<? extends Routes> rc : routes) {
        	for (IEngine engine : engines) {
	        	Routes r;
				try {
					r = rc.getConstructor(IEngine.class, IAuth.class, PageInitializer.class)
						.newInstance(engine, Auth.auth, pageInit);
				} catch (Exception e) {
					throw new RuntimeException("Can't instantiate Routes class " + rc.getName(), e);
				}
	        	list.add(r);
        	}
        }
        list.stream().sorted(Comparator.comparing(Routes::getPriority)).forEach(r -> {
			Logger.debug("<routes> " + r.getClass().getSimpleName() + ", " + r.getPriority());
			r.routes();
		});
	}

    public AppConfig getConfig() {
        return config;
    }
}
