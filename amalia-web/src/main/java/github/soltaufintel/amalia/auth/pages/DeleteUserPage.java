package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.auth.IUser;
import github.soltaufintel.amalia.web.action.Page;

public class DeleteUserPage extends Page {

	@Override
	protected void execute() {
		String id = ctx.pathParam("id");
		IUser user = auth().byId(id);
		put("id", id);
		put("login", user.getLogin());
		if ("d".equals(ctx.queryParam("m"))) { // Aktion
			auth().deleteUser(id);
			Logger.info("delete user " + id);
			ctx.redirect("/auth/user");
		}
	}
}
