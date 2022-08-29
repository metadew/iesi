package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.server.rest.environment.EnvironmentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IEnvironmentDtoService {
    Page<EnvironmentDto> getAll(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters);

}
