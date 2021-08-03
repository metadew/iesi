package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IComponentDtoRepository {
    Page<ComponentDto> getAll(Pageable pageable, List<ComponentFilter> componentFilters);

    Page<ComponentDto> getByName(Pageable pageable, String name);

    Optional<ComponentDto> getByNameAndVersion(String name, long version);
}
