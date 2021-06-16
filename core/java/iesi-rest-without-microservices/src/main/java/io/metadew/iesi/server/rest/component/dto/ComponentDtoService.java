package io.metadew.iesi.server.rest.component.dto;

import io.metadew.iesi.server.rest.component.ComponentFilter;
import io.metadew.iesi.server.rest.component.IComponentDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComponentDtoService implements IComponentDtoService {

    private final IComponentDtoRepository componentDtoRepository;

    @Autowired
    public ComponentDtoService(IComponentDtoRepository componentDtoRepository) {
        this.componentDtoRepository = componentDtoRepository;
    }

    @Override
    public Page<ComponentDto> getAll(Pageable pageable, List<ComponentFilter> componentFilters) {
        return componentDtoRepository.getAll(pageable, componentFilters);
    }

    @Override
    public Page<ComponentDto> getByName(Pageable pageable, String name) {
        return componentDtoRepository.getByName(pageable, name);
    }

    @Override
    public Optional<ComponentDto> getByNameAndVersion(String name, long version) {
        return componentDtoRepository.getByNameAndVersion(name, version);
    }

}
