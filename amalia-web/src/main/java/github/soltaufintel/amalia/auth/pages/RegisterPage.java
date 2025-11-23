package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

public class RegisterPage extends Page {
    // TODO Es fehlt die Möglichkeit die Registrierung ganz abzuschalten. Also dass schon dass es die RouteDef nicht gibt oder dass der AuthService alle Registrierungen abweist.

    @Override
    protected void execute() {
        Logger.info(ctx.req.ip() + " | RegisterPage " + ctx.method());
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
            Logger.info("User registered: " + login + ", " + mail);
            ctx.redirect("/auth/registered");
        } else {
            put("title", "Registrierung eines neuen Benutzers");
            put("error", false);
            put("errorMsg", "");
        }
    }
}
