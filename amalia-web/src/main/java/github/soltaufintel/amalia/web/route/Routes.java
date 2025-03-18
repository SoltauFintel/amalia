package github.soltaufintel.amalia.web.route;

/**
 * Define routes
 */
public interface Routes {
    
    /**
     * Define routes
     */
    void routes();
    
    /**
     * @return the higher the number the later the processing
     */
    int getPriority();
}
