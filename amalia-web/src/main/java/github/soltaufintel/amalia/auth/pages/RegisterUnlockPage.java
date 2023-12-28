package github.soltaufintel.amalia.auth.pages;

import github.soltaufintel.amalia.web.action.Page;

/**
 * Registrierung per Mail-Link freischalten
 */
public class RegisterUnlockPage extends Page {

    @Override
    protected void execute() {
        auth().registerUnlock(ctx.queryParam("id"));
    }
}
