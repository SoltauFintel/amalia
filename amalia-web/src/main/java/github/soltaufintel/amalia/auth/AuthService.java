package github.soltaufintel.amalia.auth;

import static github.soltaufintel.amalia.auth.IUser.ADMIN_ROLE;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.AuthException.ActivationTooLateException;
import github.soltaufintel.amalia.auth.AuthException.ForgottenPasswordRequestExpiredException;
import github.soltaufintel.amalia.auth.AuthException.InvalidLoginStringException;
import github.soltaufintel.amalia.auth.AuthException.LoginAlreadyExistsException;
import github.soltaufintel.amalia.auth.AuthException.MissingRoleException;
import github.soltaufintel.amalia.auth.AuthException.UnknownNotificationIdException;
import github.soltaufintel.amalia.auth.AuthException.UserDataInUnexpectedModeException;
import github.soltaufintel.amalia.auth.AuthException.UserDoesNotExistException;
import github.soltaufintel.amalia.auth.AuthException.WrongOldPasswordException;
import github.soltaufintel.amalia.auth.rememberme.RememberMe;
import github.soltaufintel.amalia.auth.webcontext.WebContext;
import github.soltaufintel.amalia.mail.Mail;
import github.soltaufintel.amalia.mail.MailSender;
import github.soltaufintel.amalia.web.action.Escaper;
import github.soltaufintel.amalia.web.config.AppConfig;

public class AuthService implements IAuthService {
    public static MailSender mailSender = new MailSender();
    private final IUserService sv;
    private final int encryptionFrequency;
    private final PasswordRules passwordRules;
    private final RememberMe rememberMe;
    private final WebContext ctx;
    private final AppConfig config;
    private boolean sendMailAllowed = true;
    
    /**
     * @param userService database access to user
     * @param encryptionFrequency secret value, usually between 7000 and 10000
     * @param passwordRules validator for login, password and mail address 
     * @param rememberMe not null
     * @param ctx context
     * @param config application configuration
     */
    public AuthService(IUserService userService, int encryptionFrequency, PasswordRules passwordRules,
            RememberMe rememberMe, WebContext ctx, AppConfig config) {
        sv = userService;
        if (encryptionFrequency < 7000) {
            throw new IllegalArgumentException("encryptionFrequency must be at least 7000!");
        }
        this.encryptionFrequency = encryptionFrequency;
        this.passwordRules = passwordRules;
        this.rememberMe = rememberMe;
        this.ctx = ctx;
        this.config = config;
    }
    
    @Override
    public String getUserId() {
        return ctx.session().getUserId();
    }

    @Override
    public String getLogin() {
        return ctx.session().getLogin();
    }

    @Override
    public IUser byId(String id) {
        IUser ret = sv.byId(id);
        if (ret == null) {
            throw new UserDoesNotExistException("User does not exist!");
        }
        return ret;
    }

    @Override
    public List<IUser> getUsers() {
        isAdmin();
        return sv.getUsers();
    }

    @Override
    public boolean login(String login, String password) {
        ok(login, "login");
        IUser user = sv.byLogin(login);
        if (user != null) {
            if (password == null || password.isBlank()) {
                return false;
            }
            String encryptedPassword = hashPassword(user, password);
            if (comparePasswords(encryptedPassword, user.getPassword())) {
                if (isUserUnlocked(user)) {
                    boolean redirect = !"false".equals(config.get("redirect-after-login", "true"));
                    login(user.getId(), user.getLogin(), ctx, rememberMe, redirect);
                    return true;
                } else {
                    Logger.info("Login " + user.getLogin() + " not possible because lock state is "
                            + (user.getLockState() == null ? null : user.getLockState().name()));
                }
            }
        }
        return false;
    }
    
    protected boolean comparePasswords(String pw, String userPw) {
        return pw.equals(userPw);
    }
    
    protected boolean isUserUnlocked(IUser user) {
        return UserLockState.UNLOCKED.equals(user.getLockState());
    }
    
    public static void login(String id, String login, WebContext ctx, RememberMe rememberMe) {
        login(id, login, ctx, rememberMe, true);
    }
    
    public static void login(String id, String login, WebContext ctx, RememberMe rememberMe, boolean redirect) {
        ctx.session().setUserId(id);
        ctx.session().setLogin(login);
        ctx.session().setLoggedIn(true);
        rememberMe.rememberMe(true, ctx, login, id);

        String path = ctx.session().getGoBackPath();
        ctx.session().setGoBackPath(null);
        if (redirect) {
            if (path == null || path.isBlank() || path.equals(ctx.path())) {
                ctx.redirect("/");
            } else {
                Logger.info("[Login] redirect to " + path);
                ctx.redirect(path);
            }
        }
    }

    @Override
    public void logout() {
        logout(ctx, rememberMe);
    }
    
    public static void logout(WebContext ctx, RememberMe rememberMe) {
        final String login = ctx.session().getLogin();
        rememberMe.forget(ctx, ctx.session().getUserId());
        ctx.session().setLoggedIn(false);
        ctx.session().setUserId(null);
        ctx.session().setLogin(null);
        if (login != null) {
            Logger.debug("User '" + login + "' logged out.");
        }
    }

    @Override
    public void register(String login, String password, String mail) {
        passwordRules.checkLogin(login);
        passwordRules.checkPassword(password);
        passwordRules.checkMailAddress(mail);
        
        if (sv.byLogin(login) != null) {
            throw new LoginAlreadyExistsException(login);
        }
        
        IUser user = sv.createUser(login, login, mail, getRegisterState());
        setNewPassword(user, password);
        sv.insert(user);
        
        if (UserLockState.REGISTERED.equals(user.getLockState())) {
            user.setMode("R"); // User registriert Modus
            user.setNotificationTimestamp(user.getCreated());
            user.setNotificationId(sv.generateNotificationId());
            sv.update(user);
            sendRegisterMail(user);
        }
    }
    
    protected void checkLoginString(String login) {
        try {
            ok(login, "login");
        } catch (Exception e) {
            throw new InvalidLoginStringException("Please enter login!");
        }
        for (int i = 0; i < login.length(); i++) {
            char c = login.charAt(i);
            if (c >= 'a' && c <= 'z') {
            } else if (c >= 'A' && c <= 'Z') {
            } else if (c >= '0' && c <= '9') {
            } else if (c == '.') {
            } else if (c == '-') {
            } else if (c == '_') {
            } else if (c == '@') {
            } else {
                throw new InvalidLoginStringException("Login contains unwanted characters!" + //
                        " Allowed: a-z A-Z 0-9 . - _ @");
            }
        }
    }

    @Override
    public void registerUnlock(String notificationId) {
        ok(notificationId, "notificationId");
        IUser user = sv.byNotificationId(notificationId);
        if (user == null) {
            throw new UnknownNotificationIdException("Unknown registration notification ID! Please register again!");
        } else if (!"R".equals(user.getMode()) || UserLockState.LOCKED.equals(user.getLockState())) {
            throw new UserDataInUnexpectedModeException("User data in unexpected mode! Please register again!");
        }
        if (isTooLate(user, "register")) {
            Logger.info("register mail timeout -> delete user #" + user.getId() + " " + user.getLogin());
            sv.delete(user.getId());
            throw new ActivationTooLateException("Registration invalid. Please register again!");
        }
        user.setLockState(getRegisterUnlockMailReceivedState());
        resetNotification(user);
        Logger.info("User '" + user.getLogin() + "' unlocked by mail.");
    }
    
    protected boolean isTooLate(IUser user, String action) {
        LocalDateTime timestamp = sv.parseDate(user.getNotificationTimestamp());
        long hours = ChronoUnit.HOURS.between(timestamp, LocalDateTime.now());
        long maxHours = config.getInt(action + "-mail.max-time", 0);
        Logger.info(action + " | timestamp: " + user.getNotificationTimestamp() + ", hours: " + hours + ", max: " + maxHours);
        return maxHours > 0 && hours > maxHours;
    }

    @Override
    public void forgotPassword(String mail) {
        ok(mail, "mail address");
        passwordRules.checkMailAddress(mail);
        List<IUser> users = sv.byMail(mail);
        if (users.isEmpty()) {
            Logger.warn("User mit dieser Emailadresse nicht im System vorhanden: " + mail);
            // Es wird so getan als wäre es erfolgreich. Dem User (bzw. potentiellen Angreifer) wird
            // nicht verraten, ob wirklich ein Mail versandt worden ist. Er soll nicht erfahren können,
            // ob es eine Emailadresse im System gibt.
            return;
        }
        for (IUser user : users) {
            user.setMode("V"); // Passwort vergessen Modus
            user.setNotificationId(sv.generateNotificationId());
            user.setNotificationTimestamp(sv.now());
            sv.update(user);
            sendForgotPasswordMail(user);
        }
        Logger.info("forgotPassword " + mail + ", size=" + users.size());
    }

    @Override
    public void checkForgottenPasswordNotificationId(String notificationId) {
        checkForgottenPasswordNotificationId2(notificationId);
    }
    
    protected IUser checkForgottenPasswordNotificationId2(String notificationId) {
        ok(notificationId, "notificationId");
        IUser user = sv.byNotificationId(notificationId);
        if (user == null) {
            throw new UnknownNotificationIdException("Unknown notification ID!");
        } else if (!"V".equals(user.getMode()) || UserLockState.LOCKED.equals(user.getLockState())) {
            throw new UserDataInUnexpectedModeException("User not in expected mode!");
        }
        if (isTooLate(user, "forgot")) {
            resetNotification(user);
            throw new ForgottenPasswordRequestExpiredException(
                    "Forgotten password change request expired! Please request again!");
        }
        return user;
    }

    @Override
    public void changeForgottenPassword(String notificationId, String newPassword) {
        passwordRules.checkPassword(newPassword);
        IUser user = checkForgottenPasswordNotificationId2(notificationId);
        setNewPassword(user, newPassword);
        resetNotification(user);
        sendChangedPasswordMail(user, ctx.ipAddress(), "you");
    }

    protected void resetNotification(IUser user) {
        user.setMode(null);
        user.setNotificationId(null);
        user.setNotificationTimestamp(null);
        sv.update(user);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        IUser user = byId(getUserId());
        String encryptedOldPassword = hashPassword(user, oldPassword);
        if (user.getPassword().equals(encryptedOldPassword)) {
            passwordRules.checkPassword(newPassword);
            setNewPassword(user, newPassword);
            sv.update(user);
            sendChangedPasswordMail(user, ctx.ipAddress(), "you");
            Logger.info("User '" + user.getLogin() + "' changed his password.");
        } else {
            throw new WrongOldPasswordException("Old password is wrong!");
        }
    }

    @Override
    public void setPassword(String userId, String newPassword) {
        isAdmin();
        
        IUser user = byId(userId);
        if (user == null ) {
            throw new RuntimeException("User not found!");
        }
        
        try {
            passwordRules.checkPassword(newPassword);
        } catch (Exception e) {
            Logger.warn(e.getMessage()); // Admin does not have to follow password rules, only warning.
        }
        setNewPassword(user, newPassword);
        sv.update(user);
        
        sendChangedPasswordMail(user, ctx.ipAddress(), "admin");
        Logger.info("New password set for user '" + user.getLogin() + "'");
    }

    @Override
    public void deleteUser(String userId) {
        isAdmin();
        sv.delete(userId);
    }

    @Override
    public void lockUser(String userId, UserLockState lockState) {
        ok(userId, "userId");
        if (lockState == null) {
            throw new IllegalArgumentException("lockState must not be null");
        }
        isAdmin();
        IUser user = byId(userId);
        user.setLockState(lockState);
        sv.update(user);
    }

    public void createAdmin() {
        IUser user = sv.createUser("admin", "Admin", null, UserLockState.UNLOCKED);
        user.getRoles().add(ADMIN_ROLE);
        user.setSalt(createSalt());
        user.setPassword(hashPassword(user, user.getLogin()));
        sv.insert(user);
    }

    protected void setNewPassword(IUser user, String newPassword) {
        user.setSalt(createSalt());
        user.setPassword(hashPassword(user, newPassword));
    }
    
    /**
     * @param user for getting SALT
     * @param password -
     * @return hashed password
     */
    protected String hashPassword(IUser user, String password) {
        return hashPassword(user.getSalt() + password, encryptionFrequency);
    }
    
    /**
     * @param password inkl. SALT
     * @param encryptionFrequency -
     * @return
     */
    public static String hashPassword(String password, int encryptionFrequency) {
        try {
            byte[] b = password.getBytes();
            MessageDigest algo = MessageDigest.getInstance("MD5");
            for (int i = 0; i < encryptionFrequency; i++) {
                b = algo.digest(b);
            }
            StringBuilder sb = new StringBuilder();
            for (byte i : b) {
                sb.append(String.format("%02X", i));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createSalt() {
        return "r" + new SecureRandom().nextInt();
    }

    /**
     * @return REGISTERED: Mail-Link-Bestätigung nach Registrierung notwendig.
     * UNLOCKED: Benutzer ist sofort nutzbar.
     * LOCKED: Admin muss Benutzer freischalten.
     */
    protected UserLockState getRegisterState() {
        return UserLockState.REGISTERED;
    }
    
    protected UserLockState getRegisterUnlockMailReceivedState() {
        return UserLockState.UNLOCKED; // oder LOCKED damit Admin noch User freischalten muss
    }

    protected void isAdmin() {
        IUser admin = byId(getUserId());
        if (!admin.getRoles().contains(ADMIN_ROLE)) {
            throw new MissingRoleException("You must be admin to execute this operation!");
        }
    }
    
    static void ok(String value, String attrName) {
        if (value == null || value.isBlank() || !Escaper.esc(value).equals(value)) {
            throw new RuntimeException("Illegal " + attrName);
        }
    }

    protected void sendRegisterMail(IUser user) {
        if (isSendMailAllowed()) {
            Mail mail = createMail(user, "register", "Registrierung");
            String url = config.get("url", "") + "/auth/rm?id=" + user.getNotificationId();
            mail.setBody(config.get("register.body", "{url}").replace("{login}", user.getLogin()).replace("{url}", url));
            sendMail(mail);
        }
    }

    protected void sendForgotPasswordMail(IUser user) {
        if (isSendMailAllowed()) {
            Mail mail = createMail(user, "forgot-password", "Passwort vergessen");
            String url = config.get("url", "") + "/auth/rp?id=" + user.getNotificationId();
            mail.setBody(config.get("forgot-password.body", "{url}").replace("{login}", user.getLogin()).replace("{url}", url));
            sendMail(mail);
        }
    }

    protected void sendChangedPasswordMail(IUser user, String ipAddress, String changedBy) {
        if (isSendMailAllowed()) {
            Mail mail = createMail(user, "changed-password", "Passwort geändert");
            mail.setBody(config.get("changed-password.body", "{login}, {changedby}, {ip}")
                    .replace("{login}", user.getLogin())
                    .replace("{changedby}", changedBy) // "you" or "admin"
                    .replace("{ip}", ipAddress));
            sendMail(mail);
        }
    }
    
    protected Mail createMail(IUser user, String keyprefix, String defaultSubject) {
        Mail mail = new Mail();
        mail.setToName(user.getName());
        mail.setToEmailaddress(user.getMailAddress());
        mail.setSendername(config.get(keyprefix + ".sender", "Amalia"));
        mail.setSubject(config.get(keyprefix + ".subject", defaultSubject));
        return mail;
    }
    
    protected void sendMail(Mail mail) {
        mailSender.send(mail, config);
    }

    public boolean isSendMailAllowed() {
        return sendMailAllowed;
    }

    public void setSendMailAllowed(boolean sendMailAllowed) {
        this.sendMailAllowed = sendMailAllowed;
    }
}
