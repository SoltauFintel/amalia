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
        ops.add(UpdateOperators.set(field, value));
        return this;
    }

    /**
     * @return number of modified documents
     */
    public long update() {
        UpdateResult r = query.query.update(new UpdateOptions().multi(true), ops.toArray(new UpdateOperator[0]));
        return r.getModifiedCount();
    }
}
