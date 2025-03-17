package github.soltaufintel.amalia.web.route;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.action.PageInitializer;
import github.soltaufintel.amalia.web.engine.IEngine;

/**
 * Define routes
 */
public interface Routes {

    void init(IEngine engine, IAuth auth, PageInitializer pageInit);
    
    /**
     * Define routes
     */
    void routes();
    
    /**
     * @return the higher the number the later the processing
     */
    int getPriority();
    
    int getPort();
}
