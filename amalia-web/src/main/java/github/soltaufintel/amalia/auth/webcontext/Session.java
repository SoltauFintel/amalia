package github.soltaufintel.amalia.auth.webcontext;

import github.soltaufintel.amalia.spark.Context;

public class Session {
    public static final String LOGGED_IN = "logged_in";
    public static final String LOGGED_IN_YES = "yes";
    public static final String USER_ATTR = "user";
    public static final String USERID_ATTR = "user_id";
    public static final String PATH = "path";
    private final Context ctx;

    public Session(Context ctx) {
        this.ctx = ctx;
    }
    
    public void setLoggedIn(boolean v) {
        set(LOGGED_IN, v ? LOGGED_IN_YES : null);
    }
    
    public boolean isLoggedIn() {
        return LOGGED_IN_YES.equals(get(LOGGED_IN));
    }
    
    public void setUserId(String userId) {
        set(USERID_ATTR, userId);
    }
    
    public String getUserId() {
        return get(USERID_ATTR);
    }
    
    public void setLogin(String userName) {
        set(USER_ATTR, userName);
    }
    
    public String getLogin() {
        return get(USER_ATTR);
    }
    
    public void setGoBackPath(String path) {
        set(PATH, path);
    }
    
    public String getGoBackPath() {
        return get(PATH);
    }
    
    protected String get(String name) {
        return ctx.req.session().attribute(name);
    }
    
    protected void set(String name, String value) {
        ctx.req.session().attribute(name, value);
    }
}
