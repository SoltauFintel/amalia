package github.soltaufintel.amalia.auth;

import github.soltaufintel.amalia.auth.pages.ChangeForgottenPasswordPage;
import github.soltaufintel.amalia.auth.pages.ChangeUserPasswordPage;
import github.soltaufintel.amalia.auth.pages.DeleteUserPage;
import github.soltaufintel.amalia.auth.pages.ForgotPasswordRequestPage;
import github.soltaufintel.amalia.auth.pages.LockUserAction;
import github.soltaufintel.amalia.auth.pages.LoginPage;
import github.soltaufintel.amalia.auth.pages.LogoutAction;
import github.soltaufintel.amalia.auth.pages.RegisterPage;
import github.soltaufintel.amalia.auth.pages.RegisterUnlockPage;
import github.soltaufintel.amalia.auth.pages.RegisteredPage;
import github.soltaufintel.amalia.auth.pages.SetPasswordPage;
import github.soltaufintel.amalia.auth.pages.UsersPage;
import github.soltaufintel.amalia.web.action.Action;
import github.soltaufintel.amalia.web.action.Page;

public class AuthPages {

    public Class<? extends Page> getLoginPageClass() {
        return LoginPage.class;
    }

    public Class<? extends Action> getLogoutActionClass() {
        return LogoutAction.class;
    }

    public Class<? extends Page> getRegisterPageClass() {
        return RegisterPage.class;
    }

    public Class<? extends Page> getRegisteredPageClass() {
        return RegisteredPage.class;
    }

    public Class<? extends Page> getRegisterUnlockPageClass() {
        return RegisterUnlockPage.class;
    }

    public Class<? extends Page> getForgotPasswordRequestPageClass() {
        return ForgotPasswordRequestPage.class;
    }

    public Class<? extends Page> getChangeForgottenPasswordPageClass() {
        return ChangeForgottenPasswordPage.class;
    }

    public Class<? extends Page> getChangeUserPasswordPageClass() {
        return ChangeUserPasswordPage.class;
    }

    public Class<? extends Page> getSetPasswordPageClass() {
        return SetPasswordPage.class;
    }

    public Class<? extends Page> getDeleteUserPageClass() {
        return DeleteUserPage.class;
    }

    public Class<? extends Page> getUsersPageClass() {
        return UsersPage.class;
    }

    public Class<? extends Action> getLockUserActionClass() {
        return LockUserAction.class;
    }
}
