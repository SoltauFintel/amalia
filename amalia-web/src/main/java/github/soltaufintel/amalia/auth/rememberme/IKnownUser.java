package github.soltaufintel.amalia.auth.rememberme;

/**
 * User information for the remember me feature
 */
public interface IKnownUser {

    String getId();
    
    String getUser();

    String getUserId();
}
