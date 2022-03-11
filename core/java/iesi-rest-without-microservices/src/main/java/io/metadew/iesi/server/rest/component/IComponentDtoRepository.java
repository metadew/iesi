package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IComponentDtoRepository {
    Page<ComponentDto> getAll(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters);

    Page<ComponentDto> getByName(Authentication authentication, Pageable pageable, String name);

    Optional<ComponentDto> getByNameAndVersion(Authentication authentication, String name, long version);
}
