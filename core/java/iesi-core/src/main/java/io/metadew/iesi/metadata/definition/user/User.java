package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends Metadata<UserKey>  {
    private String username;
    private String password;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
    private Set<RoleKey> roleKeys;

    @Builder
    public User(UserKey userKey, String username, String password, boolean enabled, boolean expired,
                boolean credentialsExpired, boolean locked, Set<RoleKey> roleKeys) {
        super(userKey);
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.expired = expired;
        this.credentialsExpired = credentialsExpired;
        this.locked = locked;
        this.roleKeys = roleKeys;
    }

}