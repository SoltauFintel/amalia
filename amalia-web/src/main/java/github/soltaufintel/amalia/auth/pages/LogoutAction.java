package github.soltaufintel.amalia.auth.pages;

import github.soltaufintel.amalia.web.action.Action;

public class LogoutAction extends Action {

	@Override
	protected void execute() {
		auth().logout();
		ctx.redirect("/");
	}
}
