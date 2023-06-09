package github.soltaufintel.amalia.mongo;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.mongodb.client.result.DeleteResult;

import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;

public class AQuery<E> {
	final Query<E> query;
	private final FindOptions options;
	
	public AQuery(Query<E> query, FindOptions options) {
		this.query = query;
		this.options = options;
	}

	public AQuery<E> eq(String field, Object val) {
		return q(Filters.eq(field, val));
	}
	
	public AQuery<E> eqIgnoreCase(String field, String val) {
		if (val == null) {
			return q(Filters.eq(field, val));
		}
		String regex = Pattern.quote(val.trim());
		return q(Filters.regex(field).pattern(regex).caseInsensitive());
	}

	public AQuery<E> ne(String field, Object val) {
		return q(Filters.ne(field, val));
	}

	public AQuery<E> lte(String field, Object val) {
		return q(Filters.lte(field, val));
	}

	public AQuery<E> lt(String field, Object val) {
		return q(Filters.lt(field, val));
	}

	public AQuery<E> gte(String field, Object val) {
		return q(Filters.gte(field, val));
	}

	public AQuery<E> gt(String field, Object val) {
		return q(Filters.gt(field, val));
	}

	public AQuery<E> nullOrEmpty(String field) {
		return q(Filters.or(
						Filters.eq(field, null),
						Filters.eq(field, "")
				));
	}

	public AQuery<E> notNullAndNotEmpty(String field) {
		return q(Filters.ne(field, null),
				 Filters.ne(field, ""));
	}

	public AQuery<E> regex(String field, Pattern regex) {
		return q(Filters.regex(field).pattern(regex));
	}

	public AQuery<E> regex(String field, String regex) {
		return q(Filters.regex(field).pattern(regex));
	}

	public AQuery<E> in(String field, Iterable<?> iterable) {
		return q(Filters.in(field, iterable));
	}

	public AQuery<E> exists(String field) {
		return q(Filters.exists(field));
	}

	public AQuery<E> query(Object... pairs) {
		AQuery<E> q = this;
		for (int i = 0; i < pairs.length; i += 2) {
			String name = (String) pairs[i];
			Object val = pairs[i + 1];
			q = eq(name, val);
		}
		return q;
	}

	public AQuery<E> order(String fields) {
		FindOptions newOptions = options == null ? new FindOptions() : options;
		newOptions = newOptions.sort(makeSortArray(fields));
		return new AQuery<E>(query, newOptions);
	}

	/**
	 * @param sort comma separated field names, prepend "-" for descending sort order
	 * @return Sort array for FindOptions.sort(Sort...)
	 */
	private Sort[] makeSortArray(String sort) {
		String[] fields = sort.split(",");
		Sort[] sorts = new Sort[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (field.startsWith("-")) {
				sorts[i] = Sort.descending(field.substring("-".length()));
			} else {
				sorts[i] = Sort.ascending(field);
			}
		}
		return sorts;
	}

	public AQuery<E> limit(int limit) {
		FindOptions newOptions = options == null ? new FindOptions() : options;
		newOptions = newOptions.limit(limit);
		return new AQuery<E>(query, newOptions);
	}

	/**
	 * include projection
	 * @param fields comma separated field names
	 * @return new query
	 */
	public AQuery<E> project(String fields) {
		FindOptions newOptions = options == null ? new FindOptions() : options;
		newOptions = newOptions.projection().include(fields.split(","));
		return new AQuery<E>(query, newOptions);
	}

	public E first() {
		return query.first();
	}

	public E limit1(String sort) {
		Iterator<E> iter = order(sort).limit(1).iterator();
		return iter.hasNext() ? iter.next() : null;
	}
	
	public List<E> list() {
		if (options == null) {
			return query.iterator().toList();
		} else {
			return query.iterator(options).toList();
		}
	}

	public Iterator<E> iterator() {
		if (options == null) {
			return query.iterator();
		} else {
			return query.iterator(options);
		}
	}

	public boolean isEmpty() {
		return query.first() == null;
	}
	
	public long size() {
		return query.count();
	}

	public AQuery<E> or(Filter... filters) {
		return q(Filters.or(filters));
	}
	
	private AQuery<E> q(Filter... filters) {
		return new AQuery<E>(query.filter(filters), options);
	}

	// TODO Morphia-interne Klasse nicht rausgeben
	public DeleteResult delete() {
		return query.delete();
	}
	
	public AUpdateOperation<E> update() {
		return new AUpdateOperation<E>(this);
	}
}
