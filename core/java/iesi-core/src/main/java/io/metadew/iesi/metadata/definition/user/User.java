package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
<<<<<<< HEAD

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
=======
import lombok.ToString;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
>>>>>>> master
public class User extends Metadata<UserKey> {

    private String username;
    private String password;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;
<<<<<<< HEAD

    @Builder
    public User(UserKey userKey, String username, String password, boolean enabled, boolean expired,
                boolean credentialsExpired, boolean locked) {
=======
    private Set<RoleKey> roleKeys;

    @Builder
    public User(UserKey userKey, String username, String password, boolean enabled, boolean expired,
                boolean credentialsExpired, boolean locked, Set<RoleKey> roleKeys) {
>>>>>>> master
        super(userKey);
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.expired = expired;
        this.credentialsExpired = credentialsExpired;
        this.locked = locked;
<<<<<<< HEAD
    }

    //TODO: Lazy loading of authorities and groups. When moving to Spring add as lazy loading
    public List<Authority> getAuthorities() {
        return new ArrayList<>();
    }

    public List<Group> getGroups() {
        return new ArrayList<>();
=======
        this.roleKeys = roleKeys;
>>>>>>> master
    }

}