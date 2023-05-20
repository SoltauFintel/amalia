package github.soltaufintel.amalia.auth.rememberme;

public interface IKnownUserDAO {

	IKnownUser get(String id);

	void save(IKnownUser knownUser);
	
	void delete(String userId);
}
