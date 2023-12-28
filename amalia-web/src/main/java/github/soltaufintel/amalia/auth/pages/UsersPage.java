package github.soltaufintel.amalia.auth.pages;

import java.util.List;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.auth.IUser;
import github.soltaufintel.amalia.auth.UserLockState;
import github.soltaufintel.amalia.web.action.Page;

public class UsersPage extends Page {

    @Override
    protected void execute() {
        List<IUser> users = auth().getUsers();
        DataList list = list("users");
        for (IUser user : users) {
            DataMap map = list.add();
            map.put("id", user.getId());
            map.put("login", user.getLogin());
            map.put("name", esc(user.getName()));
            map.put("lock", user.getLockState().name());
            map.put("canBeLocked", UserLockState.UNLOCKED.equals(user.getLockState()));
            map.put("mailAddress", esc(user.getMailAddress()));
        }
    }
}
