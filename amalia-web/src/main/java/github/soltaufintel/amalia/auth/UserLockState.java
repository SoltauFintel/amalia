package github.soltaufintel.amalia.auth;

public enum UserLockState {

    /** Benutzer wurde registriert, hat sich aber noch nicht via Mail-Link freigeschaltet. */
    REGISTERED,
    
    /** aktiver Benutzer */
    UNLOCKED,
    
    /**
     * gesperrter Benutzer.
     * Kann auch ein frisch registrierter Benutzer sein, der vom Admin noch nicht freigeschaltet worden ist.
     */
    LOCKED;
}
