package github.soltaufintel.amalia.mail;

public interface MailConfig {

    String getFromMailAddress();
    
    String getForceTo();
    
    String getServer();
    
    int getPort();
    
    String getUsername();
    
    String getPassword();
}
