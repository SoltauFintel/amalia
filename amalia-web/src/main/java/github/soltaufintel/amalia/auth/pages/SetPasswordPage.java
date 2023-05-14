package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.IUser;
import github.soltaufintel.amalia.web.action.Page;

/**
 * Admin sets password for an user
 */
public class SetPasswordPage extends Page {

	@Override
	protected void execute() {
		String id = ctx.pathParam("id");
		Logger.info("SetPasswordPage " + ctx.method() + " " + id);
		IUser user = auth().byId(id);
		if (isPOST()) {
			String pw = ctx.formParam("amalia_password");
			String pw2 = ctx.formParam("amalia_password2");
			if (!pw.equals(pw2)) {
				throw new RuntimeException("Passwörter sind nicht gleich!");
			}
			auth().setPassword(id, pw);
			ctx.redirect("/auth/user");
		} else {
			put("id", id);
			put("userLogin", user.getLogin());
			put("error", false);
			put("errorMsg", "");
		}
	}
}
