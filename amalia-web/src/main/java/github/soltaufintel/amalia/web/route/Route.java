package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.spark.Context;

public abstract class Route<R> {
    protected Context ctx;
    
    public void init(Context ctx) {
        this.ctx = ctx;
    }
    
    public R run() {
        execute();
        return render();
    }
    
    protected abstract void execute();
    
    protected abstract R render();
}
