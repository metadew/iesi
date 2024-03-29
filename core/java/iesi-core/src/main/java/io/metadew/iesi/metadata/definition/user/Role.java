package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Metadata<RoleKey> {

    private String name;
    private final TeamKey teamKey;
    private final Set<Privilege> privileges;
    private final Set<User> users;

    @Builder
    public Role(RoleKey metadataKey, String name, TeamKey teamKey, Set<Privilege> privileges, Set<User> users) {
        super(metadataKey);
        this.name = name;
        this.teamKey = teamKey;
        this.privileges = privileges;
        this.users = users;
    }

}
