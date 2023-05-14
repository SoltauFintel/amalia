package github.soltaufintel.amalia.auth.pages;

import github.soltaufintel.amalia.auth.UserLockState;
import github.soltaufintel.amalia.web.action.Action;

public class LockUserAction extends Action {

	@Override
	protected void execute() {
		String id = ctx.pathParam("id");
		if ("0".equals(ctx.queryParam("m"))) {
			auth().lockUser(id, UserLockState.UNLOCKED);
		} else {
			auth().lockUser(id, UserLockState.LOCKED);
		}
		ctx.redirect("/auth/user");
	}
}
