package io.metadew.iesi.metadata.definition.security;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SecurityGroup extends Metadata<SecurityGroupKey> {

    private final String name;
    private List<TeamKey> teamKeys;
    private List<MetadataKey> securedObjects;

    public SecurityGroup(SecurityGroupKey metadataKey, String name, List<TeamKey> teamKeys, List<MetadataKey> securedObjects) {
        super(metadataKey);
        this.name = name;
        this.teamKeys = teamKeys;
        this.securedObjects = securedObjects;
    }
}
