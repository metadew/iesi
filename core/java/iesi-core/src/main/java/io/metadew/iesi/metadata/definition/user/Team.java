package io.metadew.iesi.metadata.definition.user;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Team extends Metadata<TeamKey> {

    private String teamName;
    private final Set<SecurityGroup> securityGroups;
    private final Set<Role> roles;

    @Builder
    public Team(TeamKey teamKey, String teamName, Set<SecurityGroup> securityGroups, Set<Role> roles) {
        super(teamKey);
        this.teamName = teamName;
        this.securityGroups = securityGroups;
        this.roles = roles;
    }

}