package github.soltaufintel.amalia.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class TestUserService implements IUserService {

	@Override
	public IUser byId(String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IUser byLogin(String login) {
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
