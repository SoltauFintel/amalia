package github.soltaufintel.amalia.web.engine;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.route.RouteHandler;
import spark.Request;
import spark.Response;
import spark.Route;

public class SparkRouteAdapter implements Route {
    private final RouteHandler handler;

    public SparkRouteAdapter(RouteHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        return handler.handle(new Context(req, res));
    }
}
