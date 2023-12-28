package github.soltaufintel.amalia.auth;

public class AuthException extends RuntimeException {

    public AuthException(String msg) {
        super(msg);
    }

    public static class UserDoesNotExistException extends AuthException {

        public UserDoesNotExistException(String msg) {
            super(msg);
        }
    }

    public static class WrongOldPasswordException extends AuthException {

        public WrongOldPasswordException(String msg) {
            super(msg);
        }
    }

    public static class UnknownNotificationIdException extends AuthException {

        public UnknownNotificationIdException(String msg) {
            super(msg);
        }
    }
    
    public static class UserDataInUnexpectedModeException extends AuthException {

        public UserDataInUnexpectedModeException(String msg) {
            super(msg);
        }
    }

    public static class ActivationTooLateException extends AuthException {

        public ActivationTooLateException(String msg) {
            super(msg);
        }
    }

    public static class ForgottenPasswordRequestExpiredException extends AuthException {

        public ForgottenPasswordRequestExpiredException(String msg) {
            super(msg);
        }
    }

    public static class MissingRoleException extends AuthException {

        public MissingRoleException(String msg) {
            super(msg);
        }
    }

    public static class LoginAlreadyExistsException extends AuthException {
        private final String login;
        
        public LoginAlreadyExistsException(String login) {
            super("This login can't be used! Choose another.");
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    public static class InvalidLoginStringException extends AuthException {

        public InvalidLoginStringException(String msg) {
            super(msg);
        }
    }

    public static class InvalidMailAddressException extends AuthException {

        public InvalidMailAddressException(String msg) {
            super(msg);
        }
    }

    public static class PasswordRulesViolationException extends AuthException {

        public PasswordRulesViolationException(String msg) {
            super(msg);
        }
    }
}
