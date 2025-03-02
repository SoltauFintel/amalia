package github.soltaufintel.amalia.mongo;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.result.UpdateResult;

import dev.morphia.UpdateOptions;
import dev.morphia.query.updates.UpdateOperator;
import dev.morphia.query.updates.UpdateOperators;

public class AUpdateOperation<E> {
    private final List<UpdateOperator> ops = new ArrayList<>();
    private final AQuery<E> query;

    AUpdateOperation(AQuery<E> query) {
        this.query = query;
    }

    public AUpdateOperation<E> set(String field, Object value) {
        return _add(UpdateOperators.set(field, value));
    }

    /**
     * Deletes field.
     * @param field field name
     * @return this
     */
    public AUpdateOperation<E> unset(String field) {
        return _add(UpdateOperators.unset(field));
    }

    public AUpdateOperation<E> inc(String field) {
    	return _add(UpdateOperators.inc(field));
    }

    public AUpdateOperation<E> dec(String field) {
    	return _add(UpdateOperators.dec(field));
    }

    /**
     * Use this method for extending this class.
     * @param updateOperator use UpdateOperators to create an UpdateOperator
     * @return this
     */
    public AUpdateOperation<E> _add(UpdateOperator updateOperator) {
    	ops.add(updateOperator);
    	return this;
    }

    /**
     * Call this method at the end to execute the update operation.
     * @return number of modified documents
     */
    public long update() {
        UpdateResult r = query.query.update(new UpdateOptions().multi(true), ops.toArray(new UpdateOperator[0]));
        return r.getModifiedCount();
    }
}
