package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IComponentDtoRepository {
    Page<ComponentDto> getAll(Authentication authentication, Pageable pageable, List<ComponentFilter> componentFilters);
}
