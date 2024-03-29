package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class SecurityGroupPutDtoService implements ISecurityGroupPutDtoService {
    @Override
    public SecurityGroup convertToEntity(SecurityGroupPutDto securityGroupPutDto) {

        return new SecurityGroup(
                new SecurityGroupKey(securityGroupPutDto.getId()),
                securityGroupPutDto.getName(),
                securityGroupPutDto.getTeams().stream()
                        .map(securityGroupTeamPutDto -> new TeamKey(securityGroupTeamPutDto.getId()))
                        .collect(Collectors.toSet()),
                new HashSet<>()
        );
    }
}
