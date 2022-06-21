package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.server.rest.environment.EnvironmentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IEnvironmentDtoRepository {
    Page<EnvironmentDto> getAll(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters);

    Optional<EnvironmentDto> getByName(Authentication authentication, String name);

}
