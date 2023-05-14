package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

/**
 * Change password of current user
 */
public class ChangeUserPasswordPage extends Page {

	@Override
	protected void execute() {
		Logger.info("ChangeUserPasswordPage " + ctx.method() + " " + getUserId());
		if (isPOST()) {
			String oldPassword = ctx.formParam("amalia_password0");
			String pw = ctx.formParam("amalia_password");
			String pw2 = ctx.formParam("amalia_password2");
			if (oldPassword == null || pw == null || pw2 == null) {
				throw new RuntimeException("Bitte Passwörter eingeben!");
			}
			if (!pw.equals(pw2)) {
				throw new RuntimeException("Passwörter sind nicht gleich!");
			}
			auth().changePassword(oldPassword, pw);
			ctx.redirect("/");
		} else {
			put("error",false);
			put("errorMsg", "");
		}
	}
}
