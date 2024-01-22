package github.soltaufintel.amalia.auth.simple;

import java.util.Set;

import github.soltaufintel.amalia.auth.IUser;
import github.soltaufintel.amalia.auth.UserLockState;

public class SimpleUser implements IUser {
    private final String login;
    private final String mail;
    private final String password;
    private final String salt;
    
    public SimpleUser(String login, String mail, String password, String salt) {
        this.login = login;
        this.mail = mail;
        this.password = password;
        this.salt = salt;
    }

    @Override
    public String getId() {
        return login;
    }

    @Override
    public String getCreated() {
        return null;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getName() {
        return login;
    }

    @Override
    public String getMailAddress() {
        return mail;
    }

    @Override
    public String getSalt() {
        return salt;
    }

    @Override
    public void setSalt(String salt) {
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
    }

    @Override
    public UserLockState getLockState() {
        return UserLockState.UNLOCKED;
    }

    @Override
    public void setLockState(UserLockState registerUnlockMailReceivedState) {
    }

    @Override
    public String getMode() {
        return null;
    }

    @Override
    public void setMode(String mode) {
    }

    @Override
    public String getNotificationId() {
        return null;
    }

    @Override
    public void setNotificationId(String notificationId) {
    }

    @Override
    public String getNotificationTimestamp() {
        return null;
    }

    @Override
    public void setNotificationTimestamp(String timestamp) {
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }
}
