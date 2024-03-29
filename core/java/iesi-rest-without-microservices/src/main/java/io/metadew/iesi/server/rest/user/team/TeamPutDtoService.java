package io.metadew.iesi.server.rest.user.team;

import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.configuration.user.RoleConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.RoleKey;
import io.metadew.iesi.metadata.definition.user.Team;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TeamPutDtoService implements ITeamPutDtoService {

    private final SecurityGroupConfiguration securityGroupConfiguration;
    private final RoleConfiguration roleConfiguration;

    public TeamPutDtoService(SecurityGroupConfiguration securityGroupConfiguration, RoleConfiguration roleConfiguration) {
        this.securityGroupConfiguration = securityGroupConfiguration;
        this.roleConfiguration = roleConfiguration;
    }

    @Override
    public Team convertToEntity(TeamPutDto teamPutDto) {
        return new Team(
                new TeamKey(teamPutDto.getId()),
                teamPutDto.getTeamName(),
                teamPutDto.getSecurityGroups().stream()
                        .map(securityGroupPutDto -> securityGroupConfiguration.get(new SecurityGroupKey(securityGroupPutDto.getId()))
                                .orElseThrow(() -> new MetadataDoesNotExistException(new SecurityGroupKey(securityGroupPutDto.getId()))))
                        .collect(Collectors.toSet()),
                teamPutDto.getRoles().stream()
                        .map(rolePutDto -> roleConfiguration.get(new RoleKey(rolePutDto.getId()))
                                .orElseThrow(() -> new MetadataDoesNotExistException(new RoleKey(rolePutDto.getId()))))
                        .collect(Collectors.toSet())
        );
    }
}
