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
    private boolean enabled;
    private String password;

    @Builder
    public User(UserKey userKey, String username, String password, boolean enabled) {
        super(userKey);
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    //TODO: Lazy loading of authorities and groups. When moving to Spring add as lazy loading
    public List<Authority> getAuthorities() {
        return new ArrayList<>();
    }

    public List<Group> getGroups() {
        return new ArrayList<>();
    }

}