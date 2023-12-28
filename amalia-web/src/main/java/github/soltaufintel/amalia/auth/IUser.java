package github.soltaufintel.amalia.auth;

import java.util.Set;

public interface IUser {

    String ADMIN_ROLE = "Admin";
    
    String getId();
    String getCreated();
    String getLogin();
    String getName();
    String getMailAddress();
    
    String getSalt();
    void setSalt(String salt);

    String getPassword();
    void setPassword(String password);

    UserLockState getLockState();
    void setLockState(UserLockState registerUnlockMailReceivedState);

    String getMode();
    void setMode(String mode);

    String getNotificationId();
    void setNotificationId(String notificationId);

    String getNotificationTimestamp();
    void setNotificationTimestamp(String timestamp);
    
    Set<String> getRoles();
}
