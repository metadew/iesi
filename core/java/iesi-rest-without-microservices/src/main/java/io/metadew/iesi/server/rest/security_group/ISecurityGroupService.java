package io.metadew.iesi.server.rest.security_group;

import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.definition.user.TeamKey;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ISecurityGroupService {

    Optional<SecurityGroupDto> get(String username);

    Optional<SecurityGroupDto> get(UUID id);

    Set<SecurityGroupDto> getAll();

    List<SecurityGroup> getAllRawSecurityGroups();

    boolean exists(SecurityGroupKey securityGroupKey);

    void addSecurityGroup(SecurityGroup securityGroup);

    Optional<SecurityGroup> getRawSecurityGroup(SecurityGroupKey securityGroupKey);

    Optional<SecurityGroup> getRawSecurityGroup(String securityGroupname);

    void update(SecurityGroup securityGroup);

    void delete(SecurityGroupKey securityGroupKey);

    void addTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey);

    void deleteTeam(SecurityGroupKey securityGroupKey, TeamKey teamKey);

}
