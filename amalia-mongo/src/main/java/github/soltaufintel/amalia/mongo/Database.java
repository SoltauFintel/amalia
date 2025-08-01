package github.soltaufintel.amalia.mongo;

import org.bson.Document;
import org.pmw.tinylog.Logger;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.DiscriminatorFunction;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.NamingStrategy;
import github.soltaufintel.amalia.web.config.AppConfig;

public class Database {
    private final String name;
    private MongoClient client;
    private Datastore ds;
    
    public Database(String dbhost, String name, String user, String password, Class<?> ... entityClasses) {
        this.name = name;
        client = createClient(dbhost, name, user, password);
        ds = createDatastore(client, name);
        initDatastore(ds, entityClasses);
    }
    
    public Database(DatabaseConfig c) {
        this(c.getDbhost(), c.getName(), c.getUser(), c.getPassword(), c.getEntityClasses().toArray(new Class<?>[0]));
    }
    
    protected MongoClient createClient(String dbhost, String name, String user, String password) {
        String cs = "";
        if (user != null && !user.isEmpty()) {
            cs = user + ":" + password + "@";
        }
        return MongoClients.create("mongodb://" + cs + dbhost + "/" + name);
    }
    
    protected Datastore createDatastore(MongoClient client, String name) {
        MapperOptions mapperOptions = MapperOptions.builder() // Morphia legacy mode
                .discriminatorKey("className")
                .discriminator(DiscriminatorFunction.className())
                .collectionNaming(NamingStrategy.identity())
                .propertyNaming(NamingStrategy.identity())
                .build();
        return Morphia.createDatastore(client, name, mapperOptions);
    }
    
    protected void initDatastore(Datastore ds, Class<?> ... entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            Logger.debug("mapPackageFromClass: " + entityClass.getName());
            ds.getMapper().mapPackageFromClass(entityClass);
        }
        Logger.debug("ensureIndexes");
        ds.ensureIndexes();
        Logger.debug("ensureCaps");
        ds.ensureCaps();
    }
    
    public Datastore ds() {
        return ds;
    }
    
    public void close() {
        ds = null;
        client.close();
        client = null;
    }

    public void dropCollection(String collectionName) {
        MongoDatabase db = client.getDatabase(name);
        if (db != null) {
            MongoCollection<Document> collection = db.getCollection(collectionName);
            if (collection != null) {
                collection.drop();
            }
        }
    }
    
    public static void openDatabase(AppConfig config, Class<?>... entityClasses) {
        var c = new DatabaseConfig(config);
        AbstractDAO.database = new Database(c);
        System.out.println("MongoDB database: " + c.getName() + "@" + c.getDbhost()
                + (config.hasFilledKey("dbuser")
                        ? (" with user " + c.getUser() + (config.hasFilledKey("dbpw") ? " with password" : ""))
                        : ""));
    }
    
    public GridFSDAO openGridFSDAO(String collection) {
        return new GridFSDAO(client.getDatabase(name), collection);
    }
    
    /**
     * Health check: Check if MongoDB is reachable. Sends ping.
     * @param dbhost e.g. "localhost"
     * @param adminDbName "admin"
     * @return "ok": MongoDB is reachable; "timeout": MongoDB is not reachable; "error: ...": any other error message
     */
    public static String ping(String dbhost, String adminDbName) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://" + dbhost)) {
            MongoDatabase database = mongoClient.getDatabase(adminDbName);
            database.runCommand(new org.bson.Document("ping", 1));
            return "ok";
        } catch (MongoTimeoutException e) {
            Logger.debug(e);
            return "timeout";
        } catch (Exception e) {
            Logger.debug(e);
            return "error: " + e.getMessage();
        }
    }
}
