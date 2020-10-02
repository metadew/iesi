package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Team extends Metadata<TeamKey> {

    private String teamName;
    private final List<SecurityGroupKey> securityGroupKeys;
    private final List<Role> roles;

    @Builder
    public Team(TeamKey teamKey, String teamName, List<SecurityGroupKey> securityGroupKeys, List<Role> roles) {
        super(teamKey);
        this.teamName = teamName;
        this.securityGroupKeys = securityGroupKeys;
        this.roles = roles;
    }

}