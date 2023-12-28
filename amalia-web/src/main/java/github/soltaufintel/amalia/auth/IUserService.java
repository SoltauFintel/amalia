package github.soltaufintel.amalia.auth;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserService {

    IUser byId(String userId);
    IUser byLogin(String login);
    IUser byNotificationId(String notificationId);
    List<IUser> getUsers();
    List<IUser> byMail(String mail);

    IUser createUser(String login, String name, String mailAddress, UserLockState lockState);

    void insert(IUser user);
    void update(IUser user);
    void delete(String userId);
    
    String generateNotificationId();
    String now();
    LocalDateTime parseDate(String date);
}
