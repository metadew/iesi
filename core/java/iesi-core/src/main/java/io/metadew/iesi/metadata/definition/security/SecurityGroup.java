package io.metadew.iesi.metadata.definition.security;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SecurityGroup extends Metadata<SecurityGroupKey> {

    private String name;
    private Set<TeamKey> teamKeys;
    private Set<MetadataKey> securedObjects;

    @Builder
    public SecurityGroup(SecurityGroupKey metadataKey, String name, Set<TeamKey> teamKeys, Set<MetadataKey> securedObjects) {
        super(metadataKey);
        this.name = name;
        this.teamKeys = teamKeys;
        this.securedObjects = securedObjects;
    }

}
