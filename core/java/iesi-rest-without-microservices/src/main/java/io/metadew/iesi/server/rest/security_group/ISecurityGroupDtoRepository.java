package io.metadew.iesi.server.rest.security_group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISecurityGroupDtoRepository {

    Optional<SecurityGroupDto> get(String teamName);

    Optional<SecurityGroupDto> get(UUID id);

    Page<SecurityGroupDto> getAll(Pageable pageable, List<SecurityGroupFilter> securityGroupFilters);

}
