package github.soltaufintel.amalia.auth;

import java.util.List;

import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.route.RouteHandler;

public class NoOpAuth implements IAuth {

    @Override
    public IAuthService getService(Context ctx) {
        return new IAuthService() {
            
            @Override
            public void setPassword(String userId, String newPassword) {
            }
            
            @Override
            public void registerUnlock(String notificationId) {
            }
            
            @Override
            public void register(String login, String password, String mail) {
            }
            
            @Override
            public void logout() {
            }
            
            @Override
            public boolean login(String login, String password) {
                return false;
            }
            
            @Override
            public void lockUser(String userId, UserLockState lockState) {
            }
            
            @Override
            public void forgotPassword(String mail) {
            }
            
            @Override
            public void deleteUser(String userId) {
            }
            
            @Override
            public void checkForgottenPasswordNotificationId(String notificationId) {
            }
            
            @Override
            public void changePassword(String oldPassword, String newPassword) {
            }
            
            @Override
            public void changeForgottenPassword(String notificationId, String newPassword) {
            }

            @Override
            public String getUserId() {
                return null;
            }

            @Override
            public String getLogin() {
                return null;
            }

            @Override
            public IUser byId(String id) {
                return null;
            }

            @Override
            public List<IUser> getUsers() {
                return null;
            }
        };
    }

    public static class NoOpAuthRoutes implements IAuthRoutes {

        @Override
        public void routes() {
        }
        
        @Override
        public int getPriority() {
            return 0;
        }
        
        @Override
        public RouteHandler getLoginPageRouteHandler() {
            return null;
        }
    }
    
    @Override
    public Class<? extends IAuthRoutes> getRoutes() {
        return NoOpAuthRoutes.class;
    }

    @Override
    public void addNotProtected(String path) {
    }

    @Override
    public void filter(WebContext ctx) {
    }
}
