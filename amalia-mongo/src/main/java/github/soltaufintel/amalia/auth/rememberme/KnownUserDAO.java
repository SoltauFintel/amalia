package github.soltaufintel.amalia.auth.rememberme;

import github.soltaufintel.amalia.mongo.AbstractDAO;

public class KnownUserDAO extends AbstractDAO<KnownUser> implements IKnownUserDAO {

    @Override
    protected Class<KnownUser> getEntityClass() {
        return KnownUser.class;
    }

    /**
     * @param userId contains service name and foreign user id
     */
    @Override
    public void delete(String userId) {
    	eq("userId", userId).delete();
    }

	@Override
	public void save(IKnownUser knownUser) {
		super.save((KnownUser) knownUser);
	}
}
