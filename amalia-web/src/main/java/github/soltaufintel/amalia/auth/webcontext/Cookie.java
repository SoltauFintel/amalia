package github.soltaufintel.amalia.auth.webcontext;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.spark.Context;

public class Cookie {
    private final String name;
    private final Context ctx;
    
    public Cookie(String name, Context ctx) {
        this.name = name;
        this.ctx = ctx;
    }

    public String get() {
        String ret = ctx.req.cookie(name);
        Logger.trace("get Cookie " + name + ": " + ret);
        return ret;
    }

    public void set(String value, String action) {
        ctx.res.cookie("", "/", name, value, 60 * 60 * 24 * 30 /* 30 days */, false, false);
        Logger.trace("Cookie " + name + " set [" + action + "]: " + value);
    }

    public void extendLifeTime(String value) {
        set(value, "extends-cookie-life-time");
    }

    public void remove() {
        ctx.res.removeCookie(name);
        Logger.trace("Cookie removed: " + name);
    }

}
