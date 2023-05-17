package github.soltaufintel.amalia.mongo;

import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.DeleteResult;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;

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
	
	// TODO Morphia-interne Klasse nicht rausgeben
	public DeleteResult delete(E entity) {
		return ds().delete(entity);
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
		return ds().getDatabase().runCommand(new Document("dbStats", 1)).getDouble("storageSize");
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
		return database.ds().find(getEntityClass());
	}
	
	protected final AQuery<E> cq() {
		return new AQuery<E>(createQuery(), null);
	}

	protected AQuery<E> query(Object... pairs) {
		return cq().query(pairs);
	}

	protected final Datastore ds() {
		return database.ds();
	}

	public static String genId() {
		return IdGenerator.code6(IdGenerator.genId());
	}
}

