package github.soltaufintel.amalia.auth;

import java.util.List;

import github.soltaufintel.amalia.mongo.AbstractDAO;

public class UserDAO extends AbstractDAO<User> {

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	public User byLogin(String login) {
		return firstIgnoreCase("login", login);
	}

	public User byNotificationId(String notificationId) {
		return first("notificationId", notificationId);
	}

	public List<User> byMail(String mailAddress) {
		return eqIgnoreCase("mailAddress", mailAddress).list();
	}
}
