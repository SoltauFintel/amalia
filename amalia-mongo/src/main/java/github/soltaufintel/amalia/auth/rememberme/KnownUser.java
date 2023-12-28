package github.soltaufintel.amalia.auth.rememberme;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;

/**
 * User information for the remember me feature
 */
@Entity
@Indexes({@Index(
            fields = { @Field("createdAt") },
            options = @IndexOptions(expireAfterSeconds = 60 * 60 * 24 * 30 /* 30 days */
        )) })
public class KnownUser implements IKnownUser {
    @Id
    private String id;
    private String user;
    private String userId;
    private java.util.Date createdAt;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
}
