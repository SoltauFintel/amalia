package github.soltaufintel.amalia.mongo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

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

    /**
     * Sometimes you can not specify a delete expression as a query
     * and you need a Predicate for each document.
     * @param test if predicate returns true the document will be deleted
     * @return number of deleted documents
     */
	public int removeIf(Predicate<E> test) {
		return removeIf(iterator(), test);
	}

	public int removeIf(Iterator<E> iter, Predicate<E> test) {
		int deleted = 0;
		while (iter.hasNext()) {
			E entity = iter.next();
			if (test.test(entity)) {
				delete(entity);
				deleted++;
			}
		}
		return deleted;
	}

    public <T> Set<T> distinct(String fieldname, Class<T> datatype) {
        Set<T> ret = new TreeSet<>();
        DistinctIterable<T> distinct = ds().getCollection(getEntityClass()).distinct(fieldname, datatype);
        distinct.forEach(o -> ret.add(o));
        return ret;
    }

    /**
     * @param id ID of current document
     * @param nav "z" for backward, "v" for forward
     * @param sortfield sort order of collection (single field, type String)
     * @param sortfieldGetter function for getting content of sort field
     * @param idGetter function for getting content of ID field
     * @param locale "en" (or something else) for case-insensitive sort order, otherwise "simple" (see MongoDB collation, needs MongoDB 4)
     * @return ID of previous or next document, fallback in case of problem: id
     */
    public String paging(String id, String nav, String sortfield, Function<E, String> sortfieldGetter, Function<E, String> idGetter, String locale) {
        E akt = get(id);
        E r = null;
        if (akt == null) {
            return id;
        } else if ("z".equals(nav)) { // z=zurück
            r = cq().lt(sortfield, sortfieldGetter.apply(akt)).locale(locale).limit1("-" + sortfield);
        } else { // v=vor
            r = cq().gt(sortfield, sortfieldGetter.apply(akt)).locale(locale).limit1(sortfield);
        }
        return r == null ? id : idGetter.apply(r);
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

