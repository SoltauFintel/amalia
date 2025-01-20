package github.soltaufintel.amalia.mongo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.DistinctIterable;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import github.soltaufintel.amalia.base.IdGenerator;

public abstract class AbstractDAO<E> {
    public static Database database;
    
    protected abstract Class<E> getEntityClass();

    public boolean save(E entity) {
        try {
            ds().save(entity);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }
    
    /**
     * @return number of deleted documents
     */
    public long delete(E entity) {
        return ds().delete(entity).getDeletedCount();
    }

    /**
     * Find by id
     * 
     * @param id String
     * @return null if nonexistent
     */
    public E get(String id) {
        return query("id", id).first();
    }

    /**
     * @param id ObjectId as hex string
     * @return null if nonexistent
     */
    public E byObjectId(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            return createQuery().filter(Filters.eq("_id", objectId)).first();
        } catch (Exception e) { // invalid ObjectId o.ä.
            return null;
        }
    }

    /**
     * @return all entities of the collection
     */
    public List<E> list() {
        return createQuery().iterator().toList();
    }
    
    public List<E> list(String sort) {
        return cq().order(sort).list();
    }
    
    public List<E> list(Object... pairs) {
        return query(pairs).list();
    }
    
    public Iterator<E> iterator() {
        return createQuery().iterator();
    }
    
    public E first(Object... pairs) {
        return query(pairs).first();
    }

    public E firstIgnoreCase(String field, String val) {
        return eqIgnoreCase(field, val).first();
    }

    public <T> Set<T> distinct(String fieldname, Class<T> datatype) {
        Set<T> ret = new TreeSet<>();
        DistinctIterable<T> distinct = ds().getCollection(getEntityClass()).distinct(fieldname, datatype);
        distinct.forEach(o -> ret.add(o));
        return ret;
    }

    public long size() {
        return createQuery().count();
    }

    public long estimatedSize() {
        return ds().getCollection(getEntityClass()).estimatedDocumentCount();
    }
    
    /**
     * @return size of whole database in Bytes
     */
    public Double getDatabaseSize() {
        Document cmd = new Document("dbStats", 1);
        Document ret = runCommand(cmd);
        return ret.getDouble("storageSize");
    }
    
    /**
     * Reduce collection size. The execution may take a few minutes. User needs dbAdmin role.
     * @return JSON response
     */
    public String compact() {
        Document cmd = new Document("compact", getEntityClass().getSimpleName());
        Document ret = runCommand(cmd);
        return ret.toJson();
    }

    private Document runCommand(Document cmd) {
        return ds().getDatabase().runCommand(cmd);
    }

    protected AQuery<E> eq(String field, Object val) {
        return cq().eq(field, val);
    }
    
    protected AQuery<E> eqIgnoreCase(String field, String val) {
        return cq().eqIgnoreCase(field, val);
    }

    protected AQuery<E> ne(String field, Object val) {
        return cq().ne(field, val);
    }

    protected final Query<E> createQuery() {
        return ds().find(getEntityClass());
    }
    
    protected final AQuery<E> cq() {
        return new AQuery<E>(createQuery(), null);
    }

    protected AQuery<E> query(Object... pairs) {
        return cq().query(pairs);
    }

    protected Datastore ds() {
        return database.ds();
    }

    public static String genId() {
        return IdGenerator.createId6();
    }
}

