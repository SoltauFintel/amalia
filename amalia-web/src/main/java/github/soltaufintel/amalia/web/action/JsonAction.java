package github.soltaufintel.amalia.web.action;

import com.google.gson.Gson;

import github.soltaufintel.amalia.web.route.Route;

public abstract class JsonAction<O> extends Route<String> {
    protected O result;
    
    @Override
    protected final String render() {
        ctx.res.type(getJsonContentType());
        return new Gson().toJson(result);
    }
    
    protected String getJsonContentType() {
        return "application/json";
    }
}
