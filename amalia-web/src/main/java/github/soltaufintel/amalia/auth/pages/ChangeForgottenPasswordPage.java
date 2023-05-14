package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

public class ChangeForgottenPasswordPage extends Page {

	@Override
	protected void execute() {
		String id = ctx.queryParam("id");
		auth().checkForgottenPasswordNotificationId(id);
		Logger.info("ChangeForgottenPasswordPage " + ctx.method() + " " + id);
		if (isPOST()) {
			String pw = ctx.formParam("amalia_password");
			String pw2 = ctx.formParam("amalia_password2");
			if (!pw.equals(pw2)) {
				throw new RuntimeException("Passwörter sind nicht gleich!");
			}
			auth().changeForgottenPassword(id, pw);
			ctx.redirect("/");
		} else {
			put("id", id);
			put("error", false);
			put("errorMsg", "");
		}
	}
}
