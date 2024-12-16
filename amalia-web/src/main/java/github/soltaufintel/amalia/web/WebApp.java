package github.soltaufintel.amalia.web;

import static spark.Spark.initExceptionHandler;

import java.util.Comparator;
import java.util.List;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.auth.NoOpAuth;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.builder.Banner;
import github.soltaufintel.amalia.web.builder.Initializer;
import github.soltaufintel.amalia.web.builder.LoggingInitializer;
import github.soltaufintel.amalia.web.config.AppConfig;
import github.soltaufintel.amalia.web.engine.Engine;
import github.soltaufintel.amalia.web.route.Routes;

public class WebApp {
    private final String appVersion;
    private final LoggingInitializer logging;
    private final AppConfig config;
    private final List<Initializer> initializers;
    private final List<Routes> routes;
    private final PageInitializer pageInit;
    private final Banner banner;
    
    private Engine engine;

    public WebApp(String appVersion, LoggingInitializer logging, AppConfig config,
            List<Initializer> initializers, List<Routes> routes, PageInitializer pageInit, Banner banner) {
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
        engine = startEngine(config);
        banner.print(appVersion, config, engine);
        initializers.forEach(i -> i.init(config));
        if (Auth.auth == null) {
            Logger.debug("Auth.auth is null and is set to NoOpAuth");
            Auth.auth = new NoOpAuth();
        }
        routes.stream().sorted(Comparator.comparing(Routes::getPriority)).forEach(r -> routes(r));
        banner.ready();
    }

    protected Engine startEngine(AppConfig config) {
        return new Engine(
                config.getPort(),
                config.get("static-files", "web"),
                config.get("app.name"),
                config.isDevelopment());
    }

    private void routes(Routes r) {
        Logger.debug("<routes> " + r.getClass().getSimpleName() + ", " + r.getPriority());
        r.init(engine, Auth.auth, pageInit);
        r.routes();
    }
    
    public AppConfig getConfig() {
        return config;
    }
}
