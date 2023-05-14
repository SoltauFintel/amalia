package github.soltaufintel.amalia.auth;

import java.util.Set;
import java.util.TreeSet;

public class TestUser implements IUser {
	private String id;
	private String login;
	private String salt;
	private String password;
	private String mailAddress;
	private String name;
	private String created;
	private UserLockState lockState;
	private String mode;
	private String notificationId;
	private String notificationTimestamp;
	private String comment;
	private final Set<String> roles = new TreeSet<>();
	
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String getSalt() {
		return salt;
	}

	@Override
	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	@Override
	public UserLockState getLockState() {
		return lockState;
	}

	@Override
	public void setLockState(UserLockState lockState) {
		this.lockState = lockState;
	}

	@Override
	public String getMode() {
		return mode;
	}

	@Override
	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String getNotificationId() {
		return notificationId;
	}

	@Override
	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	@Override
	public String getNotificationTimestamp() {
		return notificationTimestamp;
	}

	@Override
	public void setNotificationTimestamp(String notificationTimestamp) {
		this.notificationTimestamp = notificationTimestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}
}
