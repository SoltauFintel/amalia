package github.soltaufintel.amalia.auth.simple;

import java.time.LocalDateTime;
import java.util.List;

import github.soltaufintel.amalia.auth.IUser;
import github.soltaufintel.amalia.auth.IUserService;
import github.soltaufintel.amalia.auth.UserLockState;
import github.soltaufintel.amalia.web.config.AppConfig;

public class SimpleUserService implements IUserService {
    private static final SimpleUser user = user();
    
    private static SimpleUser user() {
        AppConfig c = new AppConfig();
        return new SimpleUser(c.get("user.login"), c.get("user.mail"), c.get("user.password"), c.get("user.salt"));
    }

    @Override
    public IUser byLogin(String login) {
        return login.equals(user.getLogin()) ? user : null;
    }

    @Override
    public IUser byId(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IUser byNotificationId(String notificationId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IUser> getUsers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IUser> byMail(String mail) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IUser createUser(String login, String name, String mailAddress, UserLockState lockState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(IUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(IUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generateNotificationId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String now() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDateTime parseDate(String date) {
        throw new UnsupportedOperationException();
    }
}
