package github.soltaufintel.amalia.web.builder;

import github.soltaufintel.amalia.auth.IAuth;
import github.soltaufintel.amalia.web.config.AppConfig;

/**
 * Gets AppConfig and returns new created IAuth instance
 */
public interface InitAuth {

    IAuth createIAuth(AppConfig config);
}
