package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

public class ForgotPasswordRequestPage extends Page {

    @Override
    protected void execute() {
        if (isPOST()) {
            String mail = ctx.formParam("amalia_mail");
            auth().forgotPassword(mail);
            Logger.info("ForgotPasswordRequestPage POST " + mail);
            ctx.redirect("/auth/forgot?m=i");
        } else {
            put("title", "Passwort vergessen");
            String msg = "";
            if ("i".equals(ctx.queryParam("m"))) {
                msg = "Sie erhalten eine Mail mit einem Link zur Passwort-Neuvergabe.";
            }
            put("msg", msg);
            putHas("msg", msg);
            put("error", false);
            put("errorMsg", "");
        }
    }
}
