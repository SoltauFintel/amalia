package github.soltaufintel.amalia.mongo;

import org.bson.Document;

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
    private MongoClient client;
    private Datastore ds;
    private final String name;
    
    public Database(String dbhost, String name, String user, String password, Class<?> ... entityClasses) {
        this.name = name;
        String cs = "";
        if (user != null && !user.isEmpty()) {
            cs = user + ":" + password + "@";
        }
        client = MongoClients.create("mongodb://" + cs + dbhost + "/" + name);
        MapperOptions mapperOptions = MapperOptions.builder() // Morphia legacy mode
                .discriminatorKey("className")
                .discriminator(DiscriminatorFunction.className())
                .collectionNaming(NamingStrategy.identity())
                .propertyNaming(NamingStrategy.identity())
                .build();
        ds = Morphia.createDatastore(client, name, mapperOptions);
        for (Class<?> entityClass : entityClasses) {
            ds.getMapper().mapPackageFromClass(entityClass);
        }
        ds.ensureIndexes();
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
        String dbname = config.get("dbname");
        if (dbname == null || dbname.isEmpty()) {
            throw new RuntimeException("Config parameter 'dbname' missing!");
        }
        String dbhost = config.get("dbhost", "localhost");
        String dbuser = config.get("dbuser", dbname);
        String dbpw = config.get("dbpw");
        AbstractDAO.database = new Database(dbhost, dbname, dbuser, dbpw, entityClasses);
        System.out.println("MongoDB database: " + dbname + "@" + dbhost
                + (config.hasFilledKey("dbuser")
                        ? (" with user " + dbuser + (config.hasFilledKey("dbpw") ? " with password" : ""))
                        : ""));
    }
    
    public GridFSDAO openGridFSDAO(String collection) {
        return new GridFSDAO(client.getDatabase(name), collection);
    }
}
