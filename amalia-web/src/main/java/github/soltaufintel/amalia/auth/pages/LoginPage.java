package github.soltaufintel.amalia.auth.pages;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.action.Page;

public class LoginPage extends Page {
    
    @Override
    protected void execute() {
        Logger.info("LoginPage " + ctx.method());
        if (isPOST()) {
            String user = ctx.formParam("amalia_user");
            String password = ctx.formParam("amalia_password");
            
            if (!auth().login(user, password)) {
                ctx.redirect("/login?m=f");
            }
        } else {
            put("loginError", "f".equals(ctx.queryParam("m"))); 
        }
    }
}
