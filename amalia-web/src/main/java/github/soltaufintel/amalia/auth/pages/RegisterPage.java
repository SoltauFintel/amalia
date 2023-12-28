package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

public class RegisterPage extends Page {

    @Override
    protected void execute() {
        Logger.info("RegisterPage " + ctx.method());
        if (isPOST()) {
            String login = ctx.formParam("amalia_user");
            String mail = ctx.formParam("amalia_mail");
            String pw = ctx.formParam("amalia_password");
            String pw2 = ctx.formParam("amalia_password2");
            if (login == null || mail == null || pw == null || pw2 == null) {
                throw new RuntimeException("Bitte alle Felder ausfüllen!");
            }
            if (!pw.equals(pw2)) {
                throw new RuntimeException("Passwörter sind nicht gleich!");
            }
            auth().register(login, pw, mail);
            Logger.info("User registered: " + login);
            ctx.redirect("/auth/registered");
        } else {
            put("title", "Registrierung eines neuen Benutzers");
            put("error", false);
            put("errorMsg", "");
        }
    }
}
