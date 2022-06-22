package io.metadew.iesi.server.rest.security_group;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ISecurityGroupDtoRepository {

    Optional<SecurityGroupDto> get(String teamName);

    Optional<SecurityGroupDto> get(UUID id);

    Set<SecurityGroupDto> getAll();

}
