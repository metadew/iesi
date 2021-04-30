package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.server.rest.component.ComponentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IComponentDtoService {

    Page<ComponentDto> getAll(Pageable pageable, List<ComponentFilter> componentFilters);

    Page<ComponentDto> getByName(Pageable pageable, String name);

    Optional<ComponentDto> getByNameAndVersion(String name, long version);
}
