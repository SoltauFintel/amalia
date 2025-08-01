package github.soltaufintel.amalia.mongo;

import java.util.ArrayList;
import java.util.List;

import github.soltaufintel.amalia.web.config.AppConfig;

public class DatabaseConfig {
    /** Host name of the MongoDB server, optionally with port number */
    private String dbhost = "localhost";
    /** Database name */
    private String name;
    /** User name */
    private String user;
    /** Password of the user */
    private String password;
    /** Entity classes for the mapPackageFromClass() calls */
    private List<Class<?>> entityClasses;

    public DatabaseConfig() {
    }

    public DatabaseConfig(String dbhost, String name, String user, String password, Class<?>... entityClasses) {
        this.dbhost = dbhost;
        this.name = name;
        this.user = user;
        this.password = password;
        this.entityClasses = new ArrayList<>();
        for (Class<?> c : entityClasses) {
            this.entityClasses.add(c);
        }
    }
    
    public DatabaseConfig(AppConfig config) {
        name = config.get("dbname");
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Config parameter 'dbname' missing!");
        }
        dbhost = config.get("dbhost", "localhost");
        user = config.get("dbuser", name);
        password = config.get("dbpw");
    }

    public String getDbhost() {
        return dbhost;
    }

    public void setDbhost(String dbhost) {
        this.dbhost = dbhost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    public void setEntityClasses(List<Class<?>> entityClasses) {
        this.entityClasses = entityClasses;
    }
}
