package github.soltaufintel.amalia.auth;

import java.util.List;

public interface IAuthService {

	String getUserId();
	String getLogin();
	List<IUser> getUsers();
	
	// login+logout
	boolean login(String login, String password);
	void logout();

	// register
	void register(String login, String password, String mail);
	void registerUnlock(String notificationId);
	
	// forgot password
	void forgotPassword(String mail);
	void checkForgottenPasswordNotificationId(String notificationId);
	void changeForgottenPassword(String notificationId, String newPassword);

	// change password ----
	/**
	 * Current user changes his password
	 * @param oldPassword -
	 * @param newPassword -
	 */
	void changePassword(String oldPassword, String newPassword);
	/**
	 * Admin sets new password for an user
	 * @param userId -
	 * @param newPassword -
	 */
	void setPassword(String userId, String newPassword);
	
	// lock or delete user
	IUser byId(String id);
	void deleteUser(String userId);
	void lockUser(String userId, UserLockState lockState);
	
	// TODO Weiterentwicklung: change login; forgot login; change mail address (+activation mail)
}
