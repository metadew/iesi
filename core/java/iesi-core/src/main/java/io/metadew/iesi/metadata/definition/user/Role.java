package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Metadata<RoleKey> {

    private final String name;
    private final TeamKey team;
    private final List<Privilege> privileges;
    private final List<UserKey> userKeys;

    public Role(RoleKey metadataKey, String name, TeamKey team, List<Privilege> privileges, List<UserKey> userKeys) {
        super(metadataKey);
        this.name = name;
        this.team = team;
        this.privileges = privileges;
        this.userKeys = userKeys;
    }

}
