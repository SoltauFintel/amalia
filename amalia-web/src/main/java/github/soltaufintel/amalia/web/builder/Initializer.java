package github.soltaufintel.amalia.web.builder;

import github.soltaufintel.amalia.web.config.AppConfig;

/**
 * Initialize anything during web app boot phase
 */
public interface Initializer {

    void init(AppConfig config);
}
