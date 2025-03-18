package github.soltaufintel.amalia.web.builder;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Level;

import github.soltaufintel.amalia.auth.Auth;
import github.soltaufintel.amalia.web.WebApp;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.config.AppConfig;
import github.soltaufintel.amalia.web.route.ExceptionRouteDefinition;
import github.soltaufintel.amalia.web.route.PingRouteDefinition;
import github.soltaufintel.amalia.web.route.Routes;
import github.soltaufintel.amalia.web.templating.TemplatesFoldersInitializer;
import github.soltaufintel.amalia.web.templating.TemplatesInitializer;

public class WebAppBuilder {
    private final String appVersion;
    private final List<Initializer> initializers = new ArrayList<>();
    private LoggingInitializer logging = new LoggingInitializer(Level.INFO);
    private AppConfig config = new AppConfig();
    private final List<Class<? extends Routes>> routes = new ArrayList<>();
    private PageInitializer pageInit = new PageInitializer();
    private Banner banner = new Banner();
    
    public WebAppBuilder(String appVersion) {
        this.appVersion = appVersion;
        routes.add(PingRouteDefinition.class);
        routes.add(ExceptionRouteDefinition.class);
    }

    public WebApp build() {
        return new WebApp(appVersion, logging, config, initializers, routes, pageInit, banner);
    }
    
    public WebAppBuilder withDefaultLogLevel(Level level) {
        return withLogging(new LoggingInitializer(level));
    }
    
    public WebAppBuilder withLogging(LoggingInitializer logging) {
        this.logging = logging;
        return this;
    }
    
    public WebAppBuilder withConfig(AppConfig config) {
        this.config = config;
        return this;
    }

    public WebAppBuilder withAuth(InitAuth initAuth) {
        withInitializer(config -> {
            Auth.auth = initAuth.createIAuth(config);
            routes.add(Auth.auth.getRoutes());
        });
        return this;
    }
    
    public WebAppBuilder withTemplateFiles(String... pages) {
        return withInitializer(new TemplatesInitializer(pages));
    }
    
    public WebAppBuilder withTemplatesFolders(Class<?> ref, String... folder) {
        return withInitializer(new TemplatesFoldersInitializer(ref, folder));
    }
    
    public WebAppBuilder withInitializer(Initializer initializer) {
        initializers.add(initializer);
        return this;
    }

    public WebAppBuilder withRoutes(Class<? extends Routes> routes) {
        this.routes.add(routes);
        return this;
    }

    public WebAppBuilder withErrorPage(Class<? extends ExceptionRouteDefinition> exceptionRouteDefinitionClass) {
        routes.removeIf(r -> r.getClass().getName().equals(ExceptionRouteDefinition.class.getName()));
        routes.add(exceptionRouteDefinitionClass);
        return this;
    }

    public WebAppBuilder clearRoutes() {
        routes.clear();
        return this;
    }
    
    public WebAppBuilder withPageInitializer(PageInitializer pageInit) {
        this.pageInit = pageInit;
        return this;
    }
    
    public WebAppBuilder withBanner(Banner banner) {
        this.banner = banner;
        return this;
    }
}
