package io.metadew.iesi.server.rest.environment.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentDtoService implements IEnvironmentDtoService{

    private final IEnvironmentDtoRepository environmentDtoRepository;

    @Autowired
    public EnvironmentDtoService(IEnvironmentDtoRepository environmentDtoRepository) {
        this.environmentDtoRepository = environmentDtoRepository;
    }

    @Override
    public Page<EnvironmentDto> getAll(Pageable pageable) {
        return environmentDtoRepository.getAll(pageable);
    }
}
