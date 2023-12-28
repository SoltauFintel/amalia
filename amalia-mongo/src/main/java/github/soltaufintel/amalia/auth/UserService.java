package github.soltaufintel.amalia.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import github.soltaufintel.amalia.base.IdGenerator;
import github.soltaufintel.amalia.mongo.AbstractDAO;

public class UserService implements IUserService {
    private final UserDAO dao = new UserDAO();
    
    @Override
    public IUser byLogin(String login) {
        return dao.byLogin(login);
    }

    @Override
    public IUser createUser(String login, String name, String mailAddress, UserLockState lockState) {
        User user = new User();
        user.setId(AbstractDAO.genId());
        user.setLogin(login);
        user.setName(name);
        user.setMailAddress(mailAddress);
        user.setLockState(lockState);
        user.setCreated(now());
        return user;
    }

    @Override
    public void insert(IUser user) {
        dao.save((User) user);
    }

    @Override
    public void update(IUser user) {
        dao.save((User) user);
    }

    @Override
    public IUser byId(String userId) {
        return dao.get(userId);
    }

    @Override
    public IUser byNotificationId(String notificationId) {
        return dao.byNotificationId(notificationId);
    }

    @Override
    public List<IUser> getUsers() {
        return dao.list()
                .stream()
                .sorted((a,b) -> a.getLogin().compareToIgnoreCase(b.getLogin()))
                .map(i -> (IUser) i)
                .toList();
    }

    @Override
    public List<IUser> byMail(String mail) {
        return new ArrayList<>(dao.byMail(mail));
    }

    @Override
    public void delete(String userId) {
        User user = dao.get(userId);
        if (user != null) {
            dao.delete(user);
        }
    }

    @Override
    public String generateNotificationId() {
        return IdGenerator.genId();
    }
    
    @Override
    public String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    @Override
    public LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            return null;
        }
    }
}
