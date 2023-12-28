package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.AuthException.InvalidLoginStringException;
import github.soltaufintel.amalia.auth.AuthException.InvalidMailAddressException;
import github.soltaufintel.amalia.auth.AuthException.PasswordRulesViolationException;

public class MinimalPasswordRules implements PasswordRules {

    @Override
    public void checkPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new PasswordRulesViolationException("Please enter password!");
        }
    }

    @Override
    public void checkLogin(String login) {
        try {
            AuthService.ok(login, "login");
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
    public void checkMailAddress(String mail) {
        if (mail == null || mail.isBlank() || !mail.contains("@")) {
            throw new InvalidMailAddressException("Please enter mail address!");
        }
        // Gibt es mail schon im System? (Testmodus: doppelte Emailadresse erlaubt.)
    }
}
