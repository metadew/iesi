package io.metadew.iesi.server.rest.environment.dto;

import io.metadew.iesi.server.rest.environment.EnvironmentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnWebApplication
public class EnvironmentDtoService implements IEnvironmentDtoService{

    private final IEnvironmentDtoRepository environmentDtoRepository;

    @Autowired
    public EnvironmentDtoService(IEnvironmentDtoRepository environmentDtoRepository) {
        this.environmentDtoRepository = environmentDtoRepository;
    }

    @Override
    public Page<EnvironmentDto> getAll(Authentication authentication, Pageable pageable, List<EnvironmentFilter> environmentFilters) {
        return environmentDtoRepository.getAll(authentication, pageable, environmentFilters);
    }

}
