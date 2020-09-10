package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Metadata<UserKey> {

    private String username;
    private String password;
    private boolean enabled;
    private boolean expired;
    private boolean credentialsExpired;
    private boolean locked;

    @Builder
    public User(UserKey userKey, String username, String password, boolean enabled, boolean expired,
                boolean credentialsExpired, boolean locked) {
        super(userKey);
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.expired = expired;
        this.credentialsExpired = credentialsExpired;
        this.locked = locked;
    }

    //TODO: Lazy loading of authorities and groups. When moving to Spring add as lazy loading
    public List<Authority> getAuthorities() {
        return new ArrayList<>();
    }

    public List<Group> getGroups() {
        return new ArrayList<>();
    }

}