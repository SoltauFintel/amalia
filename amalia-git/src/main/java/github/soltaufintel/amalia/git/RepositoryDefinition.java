package github.soltaufintel.amalia.git;

import java.io.File;

public interface RepositoryDefinition extends Credentials {

    String getUrl();
    
    File getLocalFolder();
}
